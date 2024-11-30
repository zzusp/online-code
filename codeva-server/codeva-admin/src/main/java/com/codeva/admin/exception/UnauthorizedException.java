package com.codeva.admin.exception;

public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        this.code = 401;
        this.message = message;
    }

    public UnauthorizedException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 401;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
