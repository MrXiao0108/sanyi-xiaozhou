package com.dzics.data.pms.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.pms.model.entity.DzDetectionTemplate;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 产品检测设置默认模板 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
public interface DzDetectionTemplateService extends IService<DzDetectionTemplate> {
    @Cacheable(cacheNames = "dzDetectionTemplCache.listDzDetectTempVo")
    List<DzDetectTempVo> listDzDetectTempVo();
}
