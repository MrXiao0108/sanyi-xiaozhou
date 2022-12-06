package com.dzics.data.common.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @Classname CacheBase
 * @Description 缓存前缀定义
 * @Date 2022/3/17 14:15
 * @Created by NeverEnd
 */
@Data
public class CacheBase {
    /**
     * 项目模块
     * 用于区分不同项目缓存前缀
     */
    @JsonIgnore
    private String projectModule = "dzics:kanban:";
}
