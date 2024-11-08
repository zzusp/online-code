import React, { useEffect } from 'react';
import { Message } from '@alifd/next';
import { default as Renderer } from './renderer';

import { useNavigate, useParams, } from "react-router-dom";
import appHelper from "../../appHelper";
const bcrypt = require('bcryptjs');
import { Base64 } from 'js-base64';

import JsxRenderer from "./jxs-render";

const Pages = () => {

  const { page: currentPage = 'login' } = useParams();

  const navigate = useNavigate();

  appHelper.utils.navigate = (path: string, options?: any) => { navigate(path, options) };
  appHelper.utils.getBcrypt = () => { return bcrypt };
  appHelper.utils.getBase64 = () => { return Base64 };
  appHelper.utils.getMessage = () => { return Message };
  appHelper.utils.renderer = (page) => {
    // 判断page是否是string
    if (typeof page === 'string') {
      return <Renderer page={page} />
    } else {
      return <JsxRenderer jsx={page} />;
    }
  };

  useEffect(() => {
    console.log("page change");
  }, [currentPage]);


  if (currentPage == 'login') {
    return (
      <div style={{ background: '#fff', height: '100%' }}>
        <Renderer page={currentPage} />
      </div>
    )
  }

  return (
    <div style={{background: "#fff"}}>
      <Renderer page={currentPage}/>
    </div>
  );
}

export default Pages;
