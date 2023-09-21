import { IPublicModelPluginContext } from '@alilc/lowcode-types';
import { Button } from '@alifd/next';
import {
  resetSchema,
} from '../../services/mockService';
import {
  saveSchema
} from '../../services/schemaService';

// 保存功能示例
const SaveSamplePlugin = (ctx: IPublicModelPluginContext | any) => {
  return {
    async init() {
      const { skeleton, hotkey, config } = ctx;

      const save = () => {
        const scenarioName = config.get('scenarioName');
        saveSchema(scenarioName);
      }

      const reset = () => {
        const scenarioName = config.get('scenarioName');
        resetSchema(scenarioName);
      }

      skeleton.add({
        name: 'saveSample',
        area: 'topArea',
        type: 'Widget',
        props: {
          align: 'right',
        },
        content: (
          <Button type="primary" onClick={() => save()}>
            保存
          </Button>
        ),
      });
      skeleton.add({
        name: 'resetSchema',
        area: 'topArea',
        type: 'Widget',
        props: {
          align: 'right',
        },
        content: (
          <Button onClick={() => reset()}>
            重置页面
          </Button>
        ),
      });
      hotkey.bind('command+s', (e: any) => {
        e.preventDefault();
        save();
      });
    },
  };
}
SaveSamplePlugin.pluginName = 'SaveSamplePlugin';
SaveSamplePlugin.meta = {
  dependencies: ['EditorInitPlugin'],
};
export default SaveSamplePlugin;
