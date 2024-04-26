package com.onlinecode.admin;

import com.slowsql.spring.boot.autoconfigure.EnableSlowSqlMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;

/**
 * @author xuan
 * @since 2020/8/16
 */
@SpringBootApplication(exclude = {ElasticsearchRestClientAutoConfiguration.class})
@EnableSlowSqlMonitor
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("onlinecode server start ok!!!");
    }

}
