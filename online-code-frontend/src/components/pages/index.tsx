import React, {useEffect, useState} from 'react';
import { Nav, Shell, Dropdown, Menu, Icon } from '@alifd/next';
import { default as Renderer } from './renderer';

import {
  Link, useNavigate,
  useParams,
} from "react-router-dom";
import appHelper from "../../appHelper";
const bcrypt = require('bcryptjs');
import { Base64 } from 'js-base64';
import {createFetch} from "../../fetchHandler";

const Pages = () => {

  const { page: currentPage = 'login' } = useParams();
  const [ menu, setMenu ] = useState([] as React.JSX.Element[]);

  const navigate = useNavigate();

  appHelper.utils.navigate = (path: string, options?: any) => { navigate(path, options) };
  appHelper.utils.getBcrypt = () => { return bcrypt };
  appHelper.utils.getBase64 = () => { return Base64 };
  appHelper.utils.renderer = (page) => {
    return <Renderer page={page} />
  };

  useEffect(() => {
    console.log("page change");
  }, [currentPage])

  if (currentPage == 'login') {
    return (
      <div style={{ background: '#fff', height: '100%' }}>
        <Renderer page={currentPage} />
      </div>
    )
  }

  async function menuInfo() {
    let data = {
      procCode: 'menuList'
    };
    await createFetch({url: '/onlinecode-api/process/run', method: 'POST', data: data})
      .then((res: any) => {
        console.log(res);
        if (res.status === 200 && res.data && res.data.code === 200) {
          const menus = res.data.data;
          setMenu(toNav(menus));
          console.log(toNav(menus));
        }
      })
      .catch((err: any) => {});
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
        if (m.mode === '0') { // schema
          arr.push(<Nav.Item key={m.code}><Link to={`/pages/` + m.code}>{m.name}</Link></Nav.Item>);
        } else if (m.mode === '1') { // react
          arr.push(<Nav.Item key={m.code}><Link to={m.url}>{m.name}</Link></Nav.Item>);
        } else if (m.mode === '2') { // iframe
          if (m.new_tab === '1') { // 新标签页
            arr.push(<Nav.Item key={m.code}><a href={m.url} target='_blank'>{m.name}</a></Nav.Item>);
          } else {
            arr.push(<Nav.Item key={m.code}><a href={m.url}>{m.name}</a></Nav.Item>);
          }
        }
      }
    });
    return arr;
  }

  async function logout() {
    await createFetch({url: '/onlinecode-api/logout', method: 'GET'})
      .then((res: any) => {})
      .catch((err: any) => {})
      .finally(() => { navigate('/pages/login') });
  }

  if (menu.length === 0) {
    menuInfo();
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
          <Dropdown trigger={
            <div style={{
              height: '52px',
              display: 'flex',
              alignItems: 'center',
              padding: '0 20px'
            }}>
              <img
                src="./img/TB1.ZBecq67gK0jSZFHXXa9jVXa-904-826.png"
                style={{
                  width: '24px',
                  height: '24px',
                  borderRadius: '50%',
                  verticalAlign: 'middle'
                }}
                alt="用户头像"
              />
              <span style={{ marginLeft: 10 }}>MyName</span>
            </div>
          } triggerType="click">
            <Menu>
              <Menu.Item onClick={() => logout()}><Icon type="exit" style={{marginRight: '1em'}} />Logout</Menu.Item>
            </Menu>
          </Dropdown>

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
