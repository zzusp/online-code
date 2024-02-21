// import { createFetchHandler } from '@alilc/lowcode-datasource-fetch-handler';

import {createFetchHandler} from "./fetchHandler";

const appHelper = {
  requestHandlersMap: {
    fetch: createFetchHandler(),
  },
  utils: {
    demoUtil: (...params: any[]) => { console.log(`this is a demoUtil with params ${params}`) },
    navigate: (path: string, options?: any) => { console.log(`not set navigate`) },
    getBcrypt: () => { console.log(`not set Bcrypt`) },
    getBase64: () => { console.log(`not set Base64`) },
    getMessage: () => { console.log(`not set Message`) },
    renderer: (page: string, onRender?: Function) => { console.log(`not set renderer`) }
  },
  constants: {
    ConstantA: 'ConstantA',
    ConstantB: 'ConstantB'
  }
};


export default appHelper;
