package com.onlinecode.admin.util;

import com.alibaba.fastjson2.JSONObject;
import com.onlinecode.admin.grpc.ProcessRunProto;
import com.onlinecode.admin.web.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 孙鹏
 * @description protobuf工具类
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
public class ProtobufUtils {

    private static final Logger log = LoggerFactory.getLogger(ProtobufUtils.class);

    public static void renderBytes(HttpServletResponse response, R<Object> r) {
        try {
            byte[] data = ProcessRunProto.RunResponse.newBuilder().setCode(r.getCode()).setMessage(r.getMessage())
                    .setData(JSONObject.toJSONString(r.getData())).build().toByteArray();
            response.setStatus(200);
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("render bytes error: {}", e.getMessage(), e);
        }
    }

}
