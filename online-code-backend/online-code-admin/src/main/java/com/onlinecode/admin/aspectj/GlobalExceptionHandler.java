package com.onlinecode.admin.aspectj;

import com.onlinecode.admin.exception.BusinessException;
import com.onlinecode.admin.web.R;
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
     * 标准化接口异常
     */
    @ExceptionHandler(BusinessException.class)
    public R<Object> handleServiceException(BusinessException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

}
