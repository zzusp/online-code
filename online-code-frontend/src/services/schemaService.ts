import {project} from '@alilc/lowcode-engine';
import {Message} from '@alifd/next';
import {IPublicEnumTransformStage, IPublicTypeProjectSchema} from '@alilc/lowcode-types';
import DefaultPageSchema from './defaultPageSchema.json';
import DefaultI18nSchema from './defaultI18nSchema.json';
import request from 'universal-request';
import {
  generateProjectSchema,
  getLSName,
  getProjectSchemaFromLocalStorage
} from "./mockService";
import { injectAssets, filterPackages } from '@alilc/lowcode-plugin-inject';
import assets from '../services/assets.json';

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
  let schema = undefined;
  let data: any = {
    procCode: 'menuGetByCode',
    vars: {
      code: scenarioName
    }
  }
  await request({url: '/onlinecode-api/process/run', method: 'POST', data: data})
    .then((res: any) => {
      if (res.status === 200 && res.data && res.data.code === 200 && res.data.data) {
        schema = JSON.parse(res.data.data.schema_json);
      }
    })
    .catch((err: any) => {
    });
  return schema;
}

const setProjectSchemaToDb = async (scenarioName: string) => {
  if (!scenarioName) {
    console.error('scenarioName is required!');
    return;
  }
  let data: any = {
    procCode: 'menuSaveSchema',
    vars: {
      code: scenarioName,
      schema: JSON.stringify(project.exportSchema(IPublicEnumTransformStage.Save))
    }
  }
  await request({url: '/onlinecode-api/process/run', method: 'POST', data: data})
    .then((res: any) => {
      if (res.status === 200 && res.data && res.data.code === 200) {
        // 删除浏览器本地缓存中的内容
        window.localStorage.removeItem(getLSName(scenarioName));
        window.localStorage.removeItem(getLSName(scenarioName, 'packages'));
        Message.success('成功保存到数据库');
      } else {
        Message.error('保存失败，错误信息：' + res.data.message);
      }
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
