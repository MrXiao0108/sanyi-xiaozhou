package com.dzics.data.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.pms.db.dao.DzDetectionTemplateDao;
import com.dzics.data.pms.model.entity.DzDetectionTemplate;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import com.dzics.data.pms.service.DzDetectionTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品检测设置默认模板 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Service
@Slf4j
public class DzDetectionTemplateServiceImpl extends ServiceImpl<DzDetectionTemplateDao, DzDetectionTemplate> implements DzDetectionTemplateService {

    @Override
    public List<DzDetectTempVo> listDzDetectTempVo() {
        return this.baseMapper.listDzDetectTempVo();
    }
}
