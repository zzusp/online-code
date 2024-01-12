import { RuntimeOptionsConfig } from '@alilc/lowcode-datasource-types';

import request from 'universal-request';
import { RequestOptions, AsObject } from 'universal-request/lib/types';
import {Message} from "@alifd/next";

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
  return new Promise<any>(((resolve, reject) => {
    request(options).then((res: any) => {
      if (res.status === 200 && res.data.code === 401) {
        Message.error(res.data.message);
        location.href = '/#/pages/login';
      } else {
        return resolve(res);
      }
    }).catch((res: any) => {
      return reject(res);
    })
  }));
}
