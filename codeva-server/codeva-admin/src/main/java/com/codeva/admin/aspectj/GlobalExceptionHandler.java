package com.codeva.admin.aspectj;

import com.alibaba.compileflow.extension.exception.CompilerException;
import com.alibaba.compileflow.extension.exception.RunCmdException;
import com.codeva.admin.exception.BusinessException;
import com.codeva.admin.exception.ForbiddenException;
import com.codeva.admin.exception.SQLErrorException;
import com.codeva.admin.exception.UnauthorizedException;
import com.codeva.admin.web.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 孙鹏
 * @description 全局异常处理器
 * @date Created in 14:11 2023/8/9
 * @modified By
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 未登录、未验证异常
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    public R<Object> handleUnauthorizedException(UnauthorizedException e) {
        log.error(e.getMessage(), e);
        return R.unauthorized();
    }

    /**
     * 权限不足异常
     */
    @ExceptionHandler(value = ForbiddenException.class)
    public R<Object> handleForbiddenExceptionException(ForbiddenException e) {
        log.error(e.getMessage(), e);
        return R.forbidden();
    }

    /**
     * 编译异常
     */
    @ExceptionHandler(value = CompilerException.class)
    public R<Object> handleCompilerException(CompilerException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 代码执行异常
     */
    @ExceptionHandler(value = RunCmdException.class)
    public R<Object> handleRumCmdException(RunCmdException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * SQL异常
     */
    @ExceptionHandler(value = SQLErrorException.class)
    public R<Object> handleSQLErrorException(SQLErrorException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 标准化接口异常
     */
    @ExceptionHandler(value = BusinessException.class)
    public R<Object> handleServiceException(BusinessException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

}
