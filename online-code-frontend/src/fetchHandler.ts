import { RuntimeOptionsConfig } from '@alilc/lowcode-datasource-types';

import request from 'universal-request';
import { RequestOptions, AsObject } from 'universal-request/lib/types';
import {Message, Dialog} from "@alifd/next";

// config 留着扩展
export function createFetchHandler(config?: Record<string, unknown>) {
  // eslint-disable-next-line space-before-function-paren
  return async function(options: RuntimeOptionsConfig) {
    const requestConfig: RequestOptions = {
      ...options,
      url: options.uri,
      method: options.method as RequestOptions['method'],
      data: options.params as AsObject,
      headers: options.headers as AsObject,
      ...config,
    };
    return await createFetch(requestConfig);
  };
}

// config 留着扩展
export async function createFetch(options: RequestOptions) {

  let proto = require('./services/protos/run');
  let run = proto.lookup('Run');
  let buffer = run.encode(options.data).finish();
  buffer = buffer.slice(0, buffer.byteOffset + buffer.byteLength);
  options.data = buffer.buffer;

  return new Promise<any>(((resolve, reject) => {
    request(options).then((res: any) => {
      if (res.status === 200) {
        if (res.data.code === 200) {
          return resolve(res);
        } else if (res.data.code === 401) {
          Dialog.confirm({
            title: '系统提示',
            content: '登录状态已过期，您可以继续留在该页面，或者重新登录',
            okProps: { children: '重新登录' },
            cancelProps: { children: '取消' },
            onOk: () => { location.href = '/#/pages/login' },
            onCancel: () => {},
            messageProps: {
              v2: true,
              type: 'warning'
            }
          });
        } else if (res.data.code === 403 || res.data.code === 500) {
          Message.error(res.data.message);
          return resolve(res);
        }
      } else {
        console.log('res error', res)
        Message.error('后端接口连接异常');
      }
    }).catch((res: any) => {
      console.log('error', res)
      let { message } = res;
      if (message == "Network Error" || message == "{\"status\":500}") {
        message = "后端接口连接异常";
      }
      else if (message.includes("timeout")) {
        message = "系统接口请求超时";
      }
      else if (message.includes("Request failed with status code")) {
        message = "系统接口" + message.substr(message.length - 3) + "异常";
      }
      Message.error(message);
    })
  }));
}
