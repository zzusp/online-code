package com.codeva.admin.config;

import com.codeva.admin.filter.RepeatableFilter;
import com.codeva.admin.interceptor.PermsInterceptor;
import com.codeva.admin.sys.service.MenuService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private final MenuService menuService;

    public SaTokenConfig(MenuService menuService) {
        this.menuService = menuService;
    }

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(new PermsInterceptor(this.menuService))
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/login", "/logout", "/isLogin");
    }

    @Bean
    public FilterRegistrationBean<RepeatableFilter> someFilterRegistration() {
        FilterRegistrationBean<RepeatableFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns("/*");
        registration.setName("repeatableFilter");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }
}