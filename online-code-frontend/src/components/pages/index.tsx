import React, {useEffect, useState} from 'react';
import {Nav, Shell, Dropdown, Menu, Icon, Message, Divider, Avatar, Overlay, Badge, Search} from '@alifd/next';
import { default as Renderer } from './renderer';
import Notice, {INotcieItem} from "./Notice";

import {
  Link, useNavigate,
  useParams,
} from "react-router-dom";
import appHelper from "../../appHelper";
const bcrypt = require('bcryptjs');
import { Base64 } from 'js-base64';
import {createFetch} from "../../fetchHandler";

import styles from './index.module.scss';
import { DEFAULT_LABEL_SIZE } from "bpmn-js/lib/util/LabelUtil";
import height = DEFAULT_LABEL_SIZE.height;
import ReactDOM from "react-dom";
import JsxRenderer from "./jxs-render";
import BoxIcon from "../third/box-icon";

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
    // 判断page是否是string
    if (typeof page === 'string') {
      return <Renderer page={page} />
    } else {
      return <JsxRenderer onRender={(ref: object) => {ReactDOM.render(page, ref.current)}}/>;
    }
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
        // let icon = <Icon type={m.icon} style={{marginRight: '8px'}} size={'small'}/>;
        let icon = <BoxIcon name={'BiHomeSmile'} style={{position: 'relative', top: '4.5px', marginRight: '8px'}} size={'20'}/>;
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
    <>
      <style>
        {`
          .page {
            /** 菜单展开 */
            .next-aside-navigation > .next-shell-navigation {
              width: 240px;
            }

            /** 菜单收起 */
            .next-aside-navigation > .next-shell-navigation.next-shell-collapse {
              width: 5.25rem !important;
            }

            .next-menu {
              /** 菜单 */
              .next-menu-item.next-nav-item {
                margin: 3px 8px 3px 8px !important;

                /* 添加过渡效果 */
                transition:
                  background-color 0.3s ease,
                  color 0.3s ease,
                  box-shadow 0.3s ease,
                  border-radius 0.3s ease,
                  transform 0.3s ease;
              }

              /** 菜单内元素 */
              .next-menu-item-inner {
                height: 45px;
                font-size: 14px;
              }

              /** 悬浮在菜单上 */
              .next-nav-item.next-menu-item:not(.next-selected):hover {
                background: #1e80ff !important;
                // background: #f2f6fa !important;
                color: #333;
                border-radius: .375rem !important;
              }

              /** 选中菜单 */
              .next-menu-item.next-nav-item.next-selected {
                background: #5584ff !important;
                color: #fff !important;
                border-radius: .375rem !important;
                font-weight: 400;
                box-shadow: 0 17px 20px -8px #4d5bec3b;
              }

              /** 选中菜单右侧蓝色竖条位置 */
              .next-nav-item.next-menu-item:before {
                right: -9px !important;
              }
            }
          }
        `}
      </style>
      <div>
        <Shell
          device={'desktop'}
          style={{border: "1px solid #eee"}}
          className="page"
        >
          <Shell.Branding>
            <div className="rectangular"></div>
            <span style={{marginLeft: 10}}>App Name</span>
          </Shell.Branding>
          <Shell.Navigation direction="hoz">
          </Shell.Navigation>

          <Shell.Action>
            <Search type="normal" shape="simple" placeholder="请输入" style={{width: "200px", marginRight: '10px'}}/>
            <Notice/>
            <div style={{height: '100%', padding: '10px', display: 'flex'}}>
              <img src="./img/github.png"
                   aria-haspopup="true" aria-expanded="false"
                   style={{height: '22px', position: 'relative', top: '-2px'}}
                   onClick={() => {
                     toGithub()
                   }}/>
            </div>
            <Popup
              trigger={
                <div className={styles.headerAvatar}>
                  <Avatar size="small" src={"./img/TB1.ZBecq67gK0jSZFHXXa9jVXa-904-826.png"} alt="用户头像"/>
                  <span style={{marginLeft: 10}}>{user?.nickName}</span>
                </div>
              }
              triggerType="click"
            >
              <div className={styles.avatarPopup}>
                <div className={styles.profile}>
                  <div className={styles.avatar}>
                    <Avatar src={"./img/TB1.ZBecq67gK0jSZFHXXa9jVXa-904-826.png"} alt="用户头像"/>
                  </div>
                  <div className={styles.content}>
                    <h4>{user?.nickName}</h4>
                    <span>{'645541506@qq.com'}</span>
                  </div>
                </div>
                <Menu className={styles.menu}>
                  <Menu.Item><Icon size="small" type="account"/>个人设置</Menu.Item>
                  <Menu.Item><Icon size="small" type="set"/>系统设置</Menu.Item>
                  <Menu.Item onClick={() => clearCache()}><Icon size="small" type="ashbin"/>清理缓存</Menu.Item>
                  <Menu.Item onClick={() => logout()}><Icon size="small" type="exit"/>退出</Menu.Item>
                </Menu>
              </div>
            </Popup>
          </Shell.Action>

          <Shell.Navigation>
            <Nav embeddable aria-label="global navigation" defaultSelectedKeys={[currentPage]}
                 activeDirection={'right'}>
              {menu.length === 0 ? '' : menu}
            </Nav>
          </Shell.Navigation>

          <Shell.Content>
            <div style={{background: "#fff"}}>
              {menu.length === 0 ? '' : <Renderer page={currentPage}/>}
            </div>
          </Shell.Content>
        </Shell>
      </div>
    </>
  );
}

export default Pages;
