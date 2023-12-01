import { createFetchHandler } from '@alilc/lowcode-datasource-fetch-handler';

const appHelper = {
  requestHandlersMap: {
    fetch: createFetchHandler()
  },
  utils: {
    demoUtil: (...params: any[]) => { console.log(`this is a demoUtil with params ${params}`) },
    navigate: (path: string, options?: any) => { console.log(`not set navigate`) },
    getBcrypt: () => { console.log(`not set Bcrypt`) },
    getBase64: () => { console.log(`not set Base64`) }
  },
  constants: {
    ConstantA: 'ConstantA',
    ConstantB: 'ConstantB'
  }
};
export default appHelper;
