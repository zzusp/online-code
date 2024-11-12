import React, {useEffect, useState} from 'react';
import {Nav, Shell, Dropdown, Menu, Icon, Message, Divider, Avatar, Overlay, Badge, Search} from '@alifd/next';
import Notice, {INotcieItem} from "./Notice";

import {
  Link, Outlet, useNavigate,
  useParams,
} from "react-router-dom";
import {createFetch} from "../../fetchHandler";

import styles from './index.module.scss';
import BoxIcon from "../third/box-icon";
import { DEFAULT_LABEL_SIZE } from "bpmn-js/lib/util/LabelUtil";
import height = DEFAULT_LABEL_SIZE.height;

const Layout = () => {

  const { page: currentPage = 'login' } = useParams();
  const [ menu, setMenu ] = useState([] as React.JSX.Element[]);
  const [ schemaMenuCode, setSchemaMenuCode ] = useState([] as string[]);

  const { Popup } = Overlay;
  const info: any = sessionStorage.getItem('currentuser');
  const user = JSON.parse(info);
  const navigate = useNavigate();


  useEffect(() => {
    console.log("page change");
    menuInfo();
  }, [currentPage]);

  function menuInfo() {
    if (info == null) {
      navigate('/pages/login');
      return;
    }
    // 取出menus中所有mode为0的菜单code
    setSchemaMenuCode(user.menus.filter((m: any) => m.mode === '0').map((m: any) => m.code));
    setMenu(toNav(user.menus));
    if (!schemaMenuCode.includes(currentPage)) {
       return (<Outlet></Outlet>);
    }
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
        let icon = <BoxIcon name={m.icon} className="menu-icon" size={'20'}/>;
        let menuContent = null
        // let icon = m.icon;
        if (m.mode === '0') { // schema
          menuContent = <Link to={`/pages/` + m.code}>{m.name}</Link>;
        } else if (m.mode === '1') { // react
          menuContent = <Link to={m.url}>{m.name}</Link>;
        } else if (m.mode === '2') { // iframe
          if (m.newTab === '1') { // 新标签页
            menuContent = <a href={m.url} target='_blank'>{m.name}</a>;
          } else {
            menuContent = <a href={m.url}>{m.name}</a>;
          }
        }
        if (menuContent != null) {
          arr.push(<Nav.Item icon={icon} key={m.code}>{menuContent}</Nav.Item>);
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
          /* 顶部 */
          .next-shell-header {
            border-bottom: unset;

            .next-shell-branding {
              height: 100%;
              margin-left: 6px;
              /* 菜单折叠按钮 */
              .nav-trigger {
                width: 38px;
                height: 38px;
                // font-size: 1.5rem;
                /* 悬浮在菜单上 */
                &:hover {
                  background: rgb(236, 242, 255) !important;
                  color: #4494f9;
                  border-radius: 50%;
                }
              }
              .app-name {
                margin-left: 10px;
                font-size: 16px;
              }
            }
          }
          .page {
            /** 菜单展开 */
            .next-aside-navigation > .next-shell-navigation {
              width: 240px;
            }

            /** 菜单收起 */
            .next-aside-navigation > .next-shell-navigation.next-shell-collapse {
              width: 5rem !important;
              > ul {
                width: 5rem !important;

                .next-menu-item-inner .menu-icon {
                  left: 3px;
                }
              }
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
                // background: #1e80ff !important;
                // background: #f2f6fa !important;
                background: rgb(236, 242, 255) !important;
                color: #4494f9;
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

              /** 菜单图标 */
              .menu-icon {
                position: relative;
                top: 4.5px;
                margin-right: 8px;
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
            <span className="app-name">App Name</span>
          </Shell.Branding>
          <Shell.Navigation direction="hoz">
          </Shell.Navigation>

          <Shell.Action>
            {/*<Search type="normal" shape="simple" placeholder="请输入" style={{width: "200px", marginRight: '10px'}}/>*/}
            <Notice/>
            {/*<div style={{height: '100%', padding: '10px', display: 'flex'}}>*/}
            {/*  <img src="./img/github.png"*/}
            {/*       aria-haspopup="true" aria-expanded="false"*/}
            {/*       style={{height: '22px', position: 'relative', top: '-2px'}}*/}
            {/*       onClick={() => {*/}
            {/*         toGithub()*/}
            {/*       }}/>*/}
            {/*</div>*/}
            <Popup
              trigger={
                <div className="header-avatar">
                  <Avatar size="small" src={"./img/TB1.ZBecq67gK0jSZFHXXa9jVXa-904-826.png"} alt="用户头像"/>
                  <span className="account-name">{user?.nickName}</span>
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
                  <Menu.Item><BoxIcon name={'BiUser'} size={'20'} className={'box-icon'} />个人设置</Menu.Item>
                  <Menu.Item><BoxIcon name={'BiCog'} size={'20'} className={'box-icon'} />系统设置</Menu.Item>
                  <Menu.Item onClick={() => clearCache()}><BoxIcon name={'BiBrushAlt'} size={'20'} className={'box-icon'} />清理缓存</Menu.Item>
                  <Divider style={{margin: '8px 0'}} />
                  <Menu.Item onClick={() => logout()}><BoxIcon name={'BiLogOut'} size={'20'} className={'box-icon'} />退出</Menu.Item>
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
            <Outlet></Outlet>
          </Shell.Content>
        </Shell>
      </div>
    </>
  );
}

export default Layout;
