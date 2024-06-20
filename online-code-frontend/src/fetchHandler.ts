import { RuntimeOptionsConfig } from '@alilc/lowcode-datasource-types';

import request from 'universal-request';
import { RequestOptions, AsObject } from 'universal-request/lib/types';
import protoRoot from './services/protos/process_run';

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
  return new Promise<any>(((resolve, reject) => {
    requestHandler(options).then((res: any) => {
      console.log('res', res);
      if (res.status === 200) {
        if (res.data.code === 200) {
          return resolve(res);
        } else if (res.data.code === 401) {
          // @ts-ignore
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

function requestHandler(options: RequestOptions) {
  if (options.url.endsWith('/run') || options.url.endsWith('/runTask') || options.url.endsWith('/runCmd')) {
    const runRequest: any = protoRoot.lookup('RunRequest');
    if (runRequest) {
      if (options.data && options.data.vars) {
        options.data.vars = JSON.stringify(options.data.vars);
      }
      let buffer = runRequest.encode(options.data).finish();
      // buffer = buffer.buffer.slice(0, buffer.byteLength);
      options.data = buffer;
    }
    let requestInit: any = {
      body: options.data,
      method: options.method,
      headers: {
        'Content-Type': 'application/octet-stream',
        'Accept': 'application/octet-stream',
        ...options.headers
      },
    };
    return new Promise((resolve, reject) => {
      fetch(options.url, requestInit).then((res: any) => {
        if (!res.ok) {
          throw new Error('Network response was not ok');
        }
        return readStream(res.body.getReader(), res);
      }).then((res: any) => {
        const runResponse: any = protoRoot.lookup('RunResponse');
        res.data = runResponse.decode(res.data);
        if (res.data && res.data.data) {
          res.data.data = JSON.parse(res.data.data);
        }
        resolve(res);
      });
    });
  } else {
    return request(options);
  }
}

function readStream(reader: ReadableStreamDefaultReader, res: any) {
  const streamToArrayBuffer = (reader: any) => {
    return new Promise((resolve, reject) => {
      const chunks: any[] = [];
      let receivedLength = 0;

      const pump = () => {
        // @ts-ignore
        reader.read().then(({ done, value }) => {
          if (done) {
            const chunksAll = new Uint8Array(receivedLength);
            let position = 0;
            for (let chunk of chunks) {
              chunksAll.set(chunk, position);
              position += chunk.length;
            }
            res.data = chunksAll;
            resolve(res);
            return;
          }

          chunks.push(value);
          receivedLength += value.length;
          pump();
        }).catch(reject);
      };

      pump();
    });
  };

  return streamToArrayBuffer(reader);
}
