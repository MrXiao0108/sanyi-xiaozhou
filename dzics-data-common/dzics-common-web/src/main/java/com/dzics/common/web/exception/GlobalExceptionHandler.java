package com.dzics.common.web.exception;

import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.CustomWarnException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public Result<String> customerException(CustomException e, HttpServletRequest req) {
        log.error("自定义错误异常 url {},errorMsg:{}", req.getRequestURI(), e.getMessage());
        return Result.error(e.getMessage());
    }
}
