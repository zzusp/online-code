// import { createFetchHandler } from '@alilc/lowcode-datasource-fetch-handler';

import {createFetchHandler} from "./fetchHandler";
import * as Next from '@alifd/next';
import * as BoxIcon from "react-icons/bi";

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
    renderer: (page: string | object, onRender?: Function) => { console.log(`not set renderer`) },
    importDependency: (dependency: string = 'Next') => {
      console.log(`importDependency method support dependency: 'Next'`);
      if (dependency === 'Next') {
        return Next
      } else if (dependency === 'BoxIcon') {
        return BoxIcon;
      }
      return null;
    }
  },
  constants: {
    ConstantA: 'ConstantA',
    ConstantB: 'ConstantB'
  }
};


export default appHelper;
