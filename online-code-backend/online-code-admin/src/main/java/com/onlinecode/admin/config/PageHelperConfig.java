package com.onlinecode.admin.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class PageHelperConfig {

    @Bean(name = "pageProperties")
    public Properties pageProperties() {
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("defaultCount", "true");
        properties.setProperty("params", "count=countsql");
        return properties;
    }

    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        interceptor.setProperties(pageProperties());
        return interceptor;
    }

}
