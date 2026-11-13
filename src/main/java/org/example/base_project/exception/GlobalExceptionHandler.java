package org.example.base_project.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.base_project.common.Result;
import org.example.base_project.utils.RequestLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理。
 *
 * 轻量级日志原则：
 * 1. 业务异常使用 warn，方便定位权限、参数、登录等问题。
 * 2. 系统异常使用 error，保留完整堆栈。
 * 3. 不输出密码、token 等敏感信息。
 */
@ControllerAdvice("org.example.base_project.controller")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result error(CustomException e, HttpServletRequest request) {
        log.warn("业务异常：code={}, msg={}, method={}, uri={}, ip={}",
                e.getCode(),
                e.getMsg(),
                request == null ? null : request.getMethod(),
                request == null ? null : request.getRequestURI(),
                RequestLogUtils.getClientIp(request));
        return Result.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseBody
    public Result handleValidationException(Exception ex, HttpServletRequest request) {
        String errorMsg = "参数校验失败";

        if (ex instanceof MethodArgumentNotValidException validEx) {
            FieldError fieldError = validEx.getBindingResult().getFieldError();
            if (fieldError != null) {
                errorMsg = fieldError.getDefaultMessage();
            }
        } else if (ex instanceof BindException bindEx) {
            FieldError fieldError = bindEx.getBindingResult().getFieldError();
            if (fieldError != null) {
                errorMsg = fieldError.getDefaultMessage();
            }
        }

        log.warn("参数校验异常：msg={}, method={}, uri={}, ip={}",
                errorMsg,
                request == null ? null : request.getMethod(),
                request == null ? null : request.getRequestURI(),
                RequestLogUtils.getClientIp(request));

        return Result.error("400", errorMsg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e, HttpServletRequest request) {
        log.error("系统异常：method={}, uri={}, ip={}",
                request == null ? null : request.getMethod(),
                request == null ? null : request.getRequestURI(),
                RequestLogUtils.getClientIp(request),
                e);
        return Result.error();
    }
}
