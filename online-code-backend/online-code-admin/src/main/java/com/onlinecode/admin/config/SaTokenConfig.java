package com.onlinecode.admin.config;

import com.onlinecode.admin.filter.RepeatableFilter;
import com.onlinecode.admin.interceptor.PermsInterceptor;
import com.onlinecode.admin.process.service.ProcessService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private final ProcessService processService;

    public SaTokenConfig(ProcessService processService) {
        this.processService = processService;
    }

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(new PermsInterceptor(processService))
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/login", "/logout", "/isLogin");
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns("/*");
        registration.setName("repeatableFilter");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }
}