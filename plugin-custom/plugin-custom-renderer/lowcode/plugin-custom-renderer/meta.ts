
import { IPublicTypeComponentMetadata, IPublicTypeSnippet } from '@alilc/lowcode-types';

const PluginCustomRendererMeta: IPublicTypeComponentMetadata = {
  "group": "低代码组件",
  "componentName": "PluginCustomRenderer",
  "title": "PluginCustomRenderer",
  "docUrl": "",
  "screenshot": "",
  "devMode": "proCode",
  "npm": {
    "package": "plugin-custom-renderer",
    "version": "0.1.0",
    "exportName": "default",
    "main": "src\\index.tsx",
    "destructuring": false,
    "subName": ""
  },
  "configure": {
    "props": [
      {
        "title": {
          "label": {
            "type": "i18n",
            "en-US": "name",
            "zh-CN": "name"
          }
        },
        "name": "name",
        "description": "低代码组件",
        "setter": [
          {
            "componentName": "StringSetter",
            "isRequired": false,
            "initialValue": {}
          },
        ],
      },
      {
        "title": {
          "label": {
            "type": "i18n",
            "en-US": "element",
            "zh-CN": "element"
          }
        },
        "name": "element",
        "description": "低代码组件",
        "setter": [
          {
            "componentName": "ObjectSetter",
            "isRequired": false,
            "initialValue": {}
          },
        ],
      },
    ],
    "supports": {
      "events": ['onRender',
        {
          "name": "onRender"
        }],
      "style": true
    },
    "component": {}
  }
};
const snippets: IPublicTypeSnippet[] = [
  {
    "title": "PluginCustomRenderer",
    "screenshot": "",
    "schema": {
      "componentName": "PluginCustomRenderer",
      "props": {}
    }
  }
];

export default {
  ...PluginCustomRendererMeta,
  snippets
};
