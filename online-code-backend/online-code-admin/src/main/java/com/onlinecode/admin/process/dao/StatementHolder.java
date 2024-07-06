package com.onlinecode.admin.process.dao;

/**
 * @author 孙鹏
 * @description 自定义Statement持有者
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
@FunctionalInterface
public interface StatementHolder<T, R> {

    R apply(T t) throws Exception;

}
