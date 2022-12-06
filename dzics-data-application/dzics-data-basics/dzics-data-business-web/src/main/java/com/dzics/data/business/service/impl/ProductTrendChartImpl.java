package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.business.model.vo.cpk.AnalysisDataVO;
import com.dzics.data.business.service.CpkService;
import com.dzics.data.business.service.ProductTrendChartService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzDetectorDataDao;
import com.dzics.data.pms.db.dao.DzProductDetectionTemplateDao;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.SelectTrendChartVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ProductTrendChartImpl implements ProductTrendChartService {
    @Autowired
    private DzProductDetectionTemplateDao templateDao;
    @Autowired
    private DzDetectorDataDao dzDetectorDataDao;
    @Autowired
    private CpkService cpkService;
    @Override
    public Result<AnalysisDataVO> list(String sub, SelectTrendChartVo chart) {
        if (StringUtils.isEmpty(chart.getProductNo())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_66);
        }
        if (chart.getDetectionId() == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_67);
        }
        String detectionId = chart.getDetectionId();
        DzProductDetectionTemplate template = templateDao.selectById(detectionId);
        if (template == null) {
            log.error("关联产品检测配置表 id不存在:{}", chart.getDetectionId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_4);
        }
        chart.setDetectionName(template.getTableColVal());
        chart.setOrderNo(template.getOrderNo());
        chart.setLineNo(template.getLineNo());
        List<BigDecimal> list = dzDetectorDataDao.selectTrendChart(chart);
        BigDecimal standardValue = template.getStandardValue();
        BigDecimal upperValue = template.getUpperValue();
        BigDecimal lowerValue = template.getLowerValue();
        if (CollectionUtils.isNotEmpty(list)) {
            double[] doubles = list.stream().mapToDouble(BigDecimal::doubleValue).toArray();
            AnalysisDataVO analysisDataVO = cpkService.analysisData(doubles, standardValue.doubleValue(), upperValue.doubleValue(), lowerValue.doubleValue());
            return new Result(CustomExceptionType.OK, analysisDataVO);
        }
        return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR17);
    }

}
