package com.dzics.data.common.base.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChengJun
 * Date 2022/1/29.
 * @since
 */
@Configuration
public class DzicsMybatisConfig {
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor();
    }

    @Bean
    public DzicsSqlInjector dzicsSqlInjector() {
        return new DzicsSqlInjector();
    }
}
