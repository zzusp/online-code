import React, { useEffect } from 'react';
import { Nav, Shell} from '@alifd/next';
import { default as Renderer } from './renderer';

import {
  Link,
  useParams,
} from "react-router-dom";

const Pages = () => {

  const { page: currentPage = 'login' } = useParams();

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
          <img
            src="https://img.alicdn.com/tfs/TB1.ZBecq67gK0jSZFHXXa9jVXa-904-826.png"
            style={{
              width: '24px',
              height: '24px',
              borderRadius: '50%',
              verticalAlign: 'middle'
            }}
            alt="用户头像"
          />
          <span style={{ marginLeft: 10 }}>MyName</span>
        </Shell.Action>

        <Shell.Navigation>
          <Nav embeddable aria-label="global navigation" defaultSelectedKeys={[currentPage]} activeDirection={'right'}>
            {/*<Nav.Item key='login'><Link to={`/pages/login`}>登录页</Link></Nav.Item>*/}
            <Nav.Item key='home'><Link to={`/pages/home`}>首页</Link></Nav.Item>
            <Nav.Item key='menu'><Link to={`/pages/menu`}>菜单管理</Link></Nav.Item>
            <Nav.Item key='process'><Link to={`/pages/process`}>流程管理</Link></Nav.Item>
            {/*<Nav.Item key='process'><Link to={`/process/design`}>流程编排</Link></Nav.Item>*/}
            <Nav.Item key='editor'><a href='/editor.html' target={'_blank'}>低代码引擎</a></Nav.Item>
          </Nav>
        </Shell.Navigation>

        <Shell.Content>
          <div style={{ background: "#fff" }}>
            <Renderer page={currentPage} />
          </div>
        </Shell.Content>
      </Shell>
    </div>
  );
}

export default Pages;
