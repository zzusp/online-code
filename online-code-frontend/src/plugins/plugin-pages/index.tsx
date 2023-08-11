import {IPublicModelPluginContext} from '@alilc/lowcode-types';
import React from 'react';
import {Nav} from '@alifd/next';
import './index.scss';
import {getProjectSchema, getProjectSchemaFromLocalStorage} from 'src/services/mockService';

const { Item } = Nav;

const PagesPlugin = (ctx: IPublicModelPluginContext) => {
  return {
    async init() {
      const { skeleton, project, config } = ctx;
      const defaultPage = 'login';
      config.set('scenarioName', defaultPage);
      config.set('scenarioDisplayName', defaultPage);
      config.set('scenarioInfo', {});

      const onSelect = async (keys: string[]) => {
        const key = keys[0];
        console.log(key)
        // 保存在 config 中用于引擎范围其他插件使用
        config.set('scenarioName', key);
        config.set('scenarioDisplayName', key);
        config.set('scenarioInfo', {});

        let scenarioSchema = await getProjectSchemaFromLocalStorage(key);
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
              <Item key='login'>登录页</Item>
              <Item key='home'>首页</Item>
              <Item key='menu'>菜单管理</Item>
              <Item key='process'>流程管理</Item>
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
