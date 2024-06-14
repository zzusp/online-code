package com.onlinecode.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

/**
 * @author 孙鹏
 * @description 使用protobuf序列化反序列化http请求参数
 * @date Created in 15:10 2024/6/14
 * @modified By
 */
@Configuration
public class ProtoBufConfig {

    @Bean
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

}
