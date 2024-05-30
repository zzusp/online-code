package com.onlinecode.admin.process.dao;

@FunctionalInterface
public interface StatementHolder<T, R> {

    R apply(T t) throws Exception;

}
