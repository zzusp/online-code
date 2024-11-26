import {IPublicModelPluginContext} from '@alilc/lowcode-types';
import React from 'react';
import {Nav} from '@alifd/next';
import './index.scss';
import {getProjectSchema, getProjectSchemaFromDb} from 'src/services/schemaService';
import {createFetch} from "../../fetchHandler";

const { Item } = Nav;

const PagesPlugin = (ctx: IPublicModelPluginContext) => {
  return {
    async init() {
      const { skeleton, project, config } = ctx;
      const defaultPage = 'login';
      config.set('scenarioName', defaultPage);
      config.set('scenarioDisplayName', defaultPage);
      config.set('scenarioInfo', {});

      let menuNav: React.JSX.Element[] = [];
      await createFetch({url: '/onlinecode-api/process/run', method: 'POST', data: {
          procCode: 'menuList'
        }})
        .then((res: any) => {
          if (res.status === 200 && res.data && res.data.code === 200) {
            const menus = res.data.data;
            menuNav = toNav(menus);
          }
        })
        .catch((err: any) => {});

      function toNav(menus: any[]) {
        if (!menus || menus.length === 0) {
          return [];
        }
        let arr: React.JSX.Element[] = [];
        menus.forEach(m => {
          // 菜单组
          if (m.type === '0') {
            arr.push(<Nav.SubNav label={m.name}>{toNav(m.children)}</Nav.SubNav>);
          } else { // 菜单
            if (m.mode === '0') { // schema
              arr.push(<Item key={m.code}>{m.name}</Item>);
            }
          }
        });
        return arr;
      }

      const onSelect = async (keys: string[]) => {
        const key = keys[0];
        console.log('selected', key);
        // 保存在 config 中用于引擎范围其他插件使用
        config.set('scenarioName', key);
        config.set('scenarioDisplayName', key);
        config.set('scenarioInfo', {});

        let scenarioSchema = await getProjectSchemaFromDb(key);
        if (!scenarioSchema) {
          scenarioSchema = await getProjectSchema();
          let schema = scenarioSchema?.componentsTree?.[0];
          if (schema) {
            scenarioSchema.componentsTree[0]['meta'] = {title: key, locator: key, router: '/' + key};
          }
        }
        // 加载schema
        project.importSchema(scenarioSchema as any);
        project.simulatorHost?.rerender();
      };
      // 注册组件面板
      const pagesPane = skeleton.add({
        area: 'leftArea',
        type: 'PanelDock',
        name: 'pagesPane',
        content: () => {
          return <Nav type='line' defaultSelectedKeys={[defaultPage]} onSelect={(e) => onSelect(e)}>
            {menuNav}
          </Nav>
        },
        contentProps: {},
        props: {
          align: 'top',
          icon: 'list',
          description: '页面管理',
        },
      }, { index: -999 });
      pagesPane?.disable?.();
      project.onSimulatorRendererReady(() => {
        pagesPane?.enable?.();
      })
    },
  };
}
PagesPlugin.pluginName = 'PagesPlugin';
export default PagesPlugin;
