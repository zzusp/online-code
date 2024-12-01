import {project} from '@alilc/lowcode-engine';
import {Message} from '@alifd/next';
import {IPublicEnumTransformStage, IPublicTypeProjectSchema} from '@alilc/lowcode-types';
import DefaultPageSchema from './defaultPageSchema.json';
import DefaultI18nSchema from './defaultI18nSchema.json';
import {
  generateProjectSchema,
  getLSName,
  getProjectSchemaFromLocalStorage
} from "./mockService";
import { injectAssets, filterPackages } from '@alilc/lowcode-plugin-inject';
import assets from '../services/assets.json';
import { post, schema } from "../fetchHandler";

export const saveSchema = async (scenarioName: string = 'unknown') => {
  console.log('save', scenarioName);
  await setProjectSchemaToDb(scenarioName);
};

export const getProjectSchemaFromDb = async (scenarioName: string) => {
  // 如果浏览器本地缓存中有，则直接取浏览器本地缓存
  const localValue = getProjectSchemaFromLocalStorage(scenarioName);
  if (localValue) {
    return localValue;
  }
  // 缓存中没有，则直接查询数据库
  let schemaJson = undefined;
  await schema(scenarioName)
    .then((res: any) => {
      // 去除转义
      const schema_json = res.data.schemaJson;
      schemaJson = JSON.parse(schema_json);
      console.log(schemaJson);
        if ('login' !== scenarioName) {
          window.localStorage.setItem(getLSName(scenarioName), schema_json);
        }
    })
    .catch((err: any) => {
    });
  return schemaJson;
}

const setProjectSchemaToDb = async (scenarioName: string) => {
  if (!scenarioName) {
    console.error('scenarioName is required!');
    return;
  }

  const schema = JSON.stringify(project.exportSchema(IPublicEnumTransformStage.Save));

  let data: any = {
    code: scenarioName,
    schema: schema
  }
  await post('menu-save-schema', data)
    .then((res: any) => {
        // 删除浏览器本地缓存中的内容
        window.localStorage.removeItem(getLSName(scenarioName));
        window.localStorage.removeItem(getLSName(scenarioName, 'packages'));
        Message.success('成功保存到数据库');
    })
    .catch((err: any) => {
    });
}

export const getPackagesFromAssets = async () => {
  const assetsObj = await injectAssets(assets);
  return await filterPackages(assetsObj?.packages);
}

export const getProjectSchema = async (scenarioName: string = 'unknown'): Promise<IPublicTypeProjectSchema> => {
  const pageSchema = await getPageSchema(scenarioName);
  return generateProjectSchema(pageSchema, DefaultI18nSchema);
};

export const getPageSchema = async (scenarioName: string = 'unknown') => {
  const schema = await getProjectSchemaFromDb(scenarioName);
  const pageSchema = schema?.componentsTree?.[0];
  if (pageSchema) {
    return pageSchema;
  }
  return DefaultPageSchema;
};
