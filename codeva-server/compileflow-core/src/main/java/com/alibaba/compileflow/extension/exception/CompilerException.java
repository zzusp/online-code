package com.alibaba.compileflow.extension.exception;

public class CompilerException extends RuntimeException {

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
    public CompilerException() {
    }

    public CompilerException(String message) {
        this.code = 500;
        this.message = message;
    }

    public CompilerException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public CompilerException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 500;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

}
