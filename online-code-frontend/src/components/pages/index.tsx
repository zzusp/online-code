import React, {useEffect, useState} from 'react';
import {Nav, Shell, Dropdown, Menu, Icon, Message, Divider, Avatar, Overlay} from '@alifd/next';
import { default as Renderer } from './renderer';

import {
  Link, useNavigate,
  useParams,
} from "react-router-dom";
import appHelper from "../../appHelper";
const bcrypt = require('bcryptjs');
import { Base64 } from 'js-base64';
import {createFetch} from "../../fetchHandler";

import styles from './index.module.css';

const Pages = () => {

  const { page: currentPage = 'login' } = useParams();
  const [ menu, setMenu ] = useState([] as React.JSX.Element[]);

  const { Popup } = Overlay;
  const info: any = sessionStorage.getItem('currentuser');
  const user = JSON.parse(info);
  const navigate = useNavigate();

  appHelper.utils.navigate = (path: string, options?: any) => { navigate(path, options) };
  appHelper.utils.getBcrypt = () => { return bcrypt };
  appHelper.utils.getBase64 = () => { return Base64 };
  appHelper.utils.getMessage = () => { return Message };
  appHelper.utils.renderer = (page) => {
    return <Renderer page={page} />
  };

  useEffect(() => {
    console.log("page change");
    menuInfo();
  }, [currentPage])


  if (currentPage == 'login') {
    return (
      <div style={{ background: '#fff', height: '100%' }}>
        <Renderer page={currentPage} />
      </div>
    )
  }

  function menuInfo() {
    if (info == null) {
      navigate('/pages/login');
      return;
    }
    setMenu(toNav(user.menus));
  }

  function toNav(menus: any[]) {
    if (!menus || menus.length === 0) {
      return [];
    }
    let arr: React.JSX.Element[] = [];
    menus.forEach(m => {
      // 菜单组
      if (m.type === '0') {
        arr.push(<Nav.SubNav label={m.name}>{toNav(m.children)}</Nav.SubNav>);
      } else if (m.type === '1') { // 菜单
        let icon = <Icon type={m.icon} style={{marginRight: '8px'}} size={'small'}/>;
        // let icon = m.icon;
        if (m.mode === '0') { // schema
          arr.push(<Nav.Item icon={icon} key={m.code}><Link to={`/pages/` + m.code}>{m.name}</Link></Nav.Item>);
        } else if (m.mode === '1') { // react
          arr.push(<Nav.Item icon={icon} key={m.code}><Link to={m.url}>{m.name}</Link></Nav.Item>);
        } else if (m.mode === '2') { // iframe
          if (m.newTab === '1') { // 新标签页
            arr.push(<Nav.Item icon={icon} key={m.code}><a href={m.url} target='_blank'>{m.name}</a></Nav.Item>);
          } else {
            arr.push(<Nav.Item icon={icon} key={m.code}><a href={m.url}>{m.name}</a></Nav.Item>);
          }
        }
      }
    });
    return arr;
  }

  function clearCache() {
    // 删除浏览器本地缓存中的内容
    window.localStorage.clear();
    Message.success("已成功清理页面缓存")
  }

  async function logout() {
    await createFetch({url: '/onlinecode-api/logout', method: 'GET'})
      .then((res: any) => {})
      .catch((err: any) => {})
      .finally(() => { navigate('/pages/login') });
  }

  function toGithub() {
    window.open("https://github.com/gitmyname/online-code/tree/dev")
  }

  return (
    <div>
      <Shell
        device={'desktop'}
        style={{ border: "1px solid #eee" }}
      >
        <Shell.Branding>
          <div className="rectangular"></div>
          <span style={{ marginLeft: 10 }}>App Name</span>
        </Shell.Branding>
        <Shell.Navigation direction="hoz">
        </Shell.Navigation>

        <Shell.Action>
          <div style={{height: '100%', padding: '10px', display: 'flex'}}>
            <svg height="26" aria-hidden="true" viewBox="0 0 16 16" version="1.1" width="26" data-view-component="true"
                 className="octicon octicon-mark-github v-align-middle color-fg-default" onClick={() => {toGithub()}}>
              <path
                d="M8 0c4.42 0 8 3.58 8 8a8.013 8.013 0 0 1-5.45 7.59c-.4.08-.55-.17-.55-.38 0-.27.01-1.13.01-2.2 0-.75-.25-1.23-.54-1.48 1.78-.2 3.65-.88 3.65-3.95 0-.88-.31-1.59-.82-2.15.08-.2.36-1.02-.08-2.12 0 0-.67-.22-2.2.82-.64-.18-1.32-.27-2-.27-.68 0-1.36.09-2 .27-1.53-1.03-2.2-.82-2.2-.82-.44 1.1-.16 1.92-.08 2.12-.51.56-.82 1.28-.82 2.15 0 3.06 1.86 3.75 3.64 3.95-.23.2-.44.55-.51 1.07-.46.21-1.61.55-2.33-.66-.15-.24-.6-.83-1.23-.82-.67.01-.27.38.01.53.34.19.73.9.82 1.13.16.45.68 1.31 2.69.94 0 .67.01 1.3.01 1.49 0 .21-.15.45-.55.38A7.995 7.995 0 0 1 0 8c0-4.42 3.58-8 8-8Z"></path>
            </svg>
          </div>
          <Popup
            trigger={
              <div className={styles.headerAvatar}>
                <Avatar size="small" src={"./img/TB1.ZBecq67gK0jSZFHXXa9jVXa-904-826.png"} alt="用户头像" />
                <span style={{ marginLeft: 10 }}>{user?.nickName}</span>
              </div>
            }
            triggerType="click"
          >
            <div className={styles.avatarPopup}>
              <div className={styles.profile}>
                <div className={styles.avatar}>
                  <Avatar src={"./img/TB1.ZBecq67gK0jSZFHXXa9jVXa-904-826.png"} alt="用户头像" />
                </div>
                <div className={styles.content}>
                  <h4>{user?.nickName}</h4>
                  <span>{'645541506@qq.com'}</span>
                </div>
              </div>
              <Menu className={styles.menu}>
                <Menu.Item><Icon size="small" type="account" />个人设置</Menu.Item>
                <Menu.Item><Icon size="small" type="set" />系统设置</Menu.Item>
                <Menu.Item onClick={() => clearCache()}><Icon size="small" type="ashbin" />清理缓存</Menu.Item>
                <Menu.Item onClick={() => logout()}><Icon size="small" type="exit" />退出</Menu.Item>
              </Menu>
            </div>
          </Popup>
        </Shell.Action>

        <Shell.Navigation>
          <Nav embeddable aria-label="global navigation" defaultSelectedKeys={[currentPage]} activeDirection={'right'}>
            {menu.length === 0 ? '' : menu}
          </Nav>
        </Shell.Navigation>

        <Shell.Content>
          <div style={{ background: "#fff" }}>
            {menu.length === 0 ? '' : <Renderer page={currentPage} />}
          </div>
        </Shell.Content>
      </Shell>
    </div>
  );
}

export default Pages;
