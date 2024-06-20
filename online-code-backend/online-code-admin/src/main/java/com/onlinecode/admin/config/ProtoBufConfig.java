package com.onlinecode.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 孙鹏
 * @description 使用protobuf序列化反序列化http请求参数
 * @date Created in 15:10 2024/6/14
 * @modified By
 */
//@Configuration
public class ProtoBufConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ProtobufHttpMessageConverter());
//        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

//    @Bean
//    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
//        return new ProtobufHttpMessageConverter();
//    }

}
