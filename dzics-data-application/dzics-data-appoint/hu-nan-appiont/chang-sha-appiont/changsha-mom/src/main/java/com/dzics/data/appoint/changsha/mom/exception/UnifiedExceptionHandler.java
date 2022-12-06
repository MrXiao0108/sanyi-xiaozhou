package com.dzics.data.appoint.changsha.mom.exception;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author LiuDongFei
 * @date 2022年06月27日 10:56
 */
@Slf4j
@Component //spring容易自动管理
@RestControllerAdvice //在Controller层添加通知，如果使用@ControllerAdvice,则不需要添加@ResponseBody
public class UnifiedExceptionHandler {
}
