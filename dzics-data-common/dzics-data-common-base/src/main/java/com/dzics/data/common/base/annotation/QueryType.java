package com.dzics.data.common.base.annotation;



import com.dzics.data.common.base.enums.QueryTypeEnu;

import java.lang.annotation.*;

/**
 * @author ZhangChengJun
 * Date 2020/10/27.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface QueryType {
    QueryTypeEnu value() default QueryTypeEnu.eq; // 拼接条件
}
