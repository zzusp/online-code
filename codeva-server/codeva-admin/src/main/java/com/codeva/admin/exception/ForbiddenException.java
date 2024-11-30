package com.codeva.admin.exception;

public class ForbiddenException extends RuntimeException {
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
    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        this.code = 403;
        this.message = message;
    }

    public ForbiddenException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 403;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
