import { IPublicModelPluginContext } from '@alilc/lowcode-types';
import { injectAssets } from '@alilc/lowcode-plugin-inject';
import assets from '../../services/assets.json';
import { getProjectSchema } from '../../services/schemaService';
import appHelper from "../../appHelper";
import ReactDOM from "react-dom";
import { renderToString } from 'react-dom/server'
import React from "react";
import Renderer from "../../components/pages/renderer";
import JsxRenderer from "../../components/pages/JxsRender";
const EditorInitPlugin = (ctx: IPublicModelPluginContext, options: any) => {
  return {
    init: async function () {
      const {material, project, config} = ctx;
      const scenarioName = config.get('scenarioName') || options['scenarioName'];
      const scenarioDisplayName = options['displayName'] || scenarioName;
      const scenarioInfo = options['info'] || {};
      // 保存在 config 中用于引擎范围其他插件使用
      config.set('scenarioName', scenarioName);
      config.set('scenarioDisplayName', scenarioDisplayName);
      config.set('scenarioInfo', scenarioInfo);
      // 设置物料描述

      await material.setAssets(await injectAssets(assets));

      const schema = await getProjectSchema(scenarioName);
      // 加载 schema
      project.importSchema(schema as any);

      const render = ReactDOM.render

      appHelper.utils.renderer = (page) => {
        // 判断page是否是string
        if (typeof page === 'string') {
          return <Renderer page={page}/>
        } else {
          return <JsxRenderer onRender={(ref: object) => {ReactDOM.render(page, ref.current)}}/>;
        }
      };
    },
  };
}
EditorInitPlugin.pluginName = 'EditorInitPlugin';
EditorInitPlugin.meta = {
  preferenceDeclaration: {
    title: '保存插件配置',
    properties: [
      {
        key: 'scenarioName',
        type: 'string',
        description: '用于localstorage存储key',
      },
      {
        key: 'displayName',
        type: 'string',
        description: '用于显示的场景名',
      },
      {
        key: 'info',
        type: 'object',
        description: '用于扩展信息',
      }
    ],
  },
};
export default EditorInitPlugin;
