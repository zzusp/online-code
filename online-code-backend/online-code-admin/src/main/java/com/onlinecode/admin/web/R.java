package com.onlinecode.admin.web;

import java.io.Serializable;

/**
 * @author Aaron.Sun
 * @description 统一API响应结果封装
 * @date Created in 17:40 2018/8/22
 * @modified by
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SUCCESS_MESSAGE = "请求成功！";
    public static final String BAD_PARAM_MESSAGE = "请求参数类型不匹配！";
    public static final String UNAUTHORIZED_MESSAGE = "您尚未登录或无操作时间过长，请重新登录！";
    public static final String LOCKED_MESSAGE = "账号已被冻结，请联系管理员！";
    public static final String FORBIDDEN_MESSAGE = "您没有足够的权限执行该操作！";
    public static final String FAIL_MESSAGE = "操作失败！";

    /**
     * 状态码
     */
    private int code;
    /**
     * 消息
     */
    private String message;
    /**
     * 结果
     */
    private T data;

    /**
     * 请求成功
     *
     * @param <T> 泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 请求成功
     *
     * @param data 结果
     * @param <T>    泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> ok(T data) {
        return new R<T>().code(200).data(data).message(SUCCESS_MESSAGE);
    }

    /**
     * 请求失败（如：请求参数类型不匹配）
     *
     * @param message 信息
     * @param <T>     泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> badParam(String message) {
        return error(400, BAD_PARAM_MESSAGE);
    }

    /**
     * 请求失败（如：未登录或未通过验证）
     *
     * @param <T> 泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> unauthorized() {
        return error(401, UNAUTHORIZED_MESSAGE);
    }

    /**
     * 请求失败（如：未登录或未通过验证）
     *
     * @param message 信息
     * @param <T>     泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 请求失败（如：账号被冻结）
     *
     * @param <T> 泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> locked() {
        return error(401, LOCKED_MESSAGE);
    }

    /**
     * 请求失败（如：权限不足）
     *
     * @param <T> 泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> forbidden() {
        return error(403, FORBIDDEN_MESSAGE);
    }

    /**
     * 请求失败（如：请求失败）
     *
     * @param <T> 泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> fail() {
        return error(FAIL_MESSAGE);
    }

    /**
     * 请求失败（如：权限不足）
     *
     * @param message 信息
     * @param <T>     泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 请求失败（如：请求资源不存在）
     *
     * @param message 信息
     * @param <T>     泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> notFind(String message) {
        return error(404, message);
    }

    /**
     * 请求失败（如：查询数据库报错）
     *
     * @param message 信息
     * @param <T>     泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> error(String message) {
        return error(500, message);
    }

    /**
     * 请求失败（如：请求频繁或系统维护）
     *
     * @param message 信息
     * @param <T>     泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> unavailable(String message) {
        return error(503, message);
    }

    /**
     * 请求失败（如：服务器异常）
     *
     * @param code  状态码
     * @param message 信息
     * @param <T>     泛型对象
     * @return 响应结果对象
     */
    public static <T> R<T> error(int code, String message) {
        return new R<T>().code(code).message(message);
    }

    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
