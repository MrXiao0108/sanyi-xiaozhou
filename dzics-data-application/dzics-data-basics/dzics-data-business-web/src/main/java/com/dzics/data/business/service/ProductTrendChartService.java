package com.dzics.data.business.service;


import com.dzics.data.business.model.vo.cpk.AnalysisDataVO;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.vo.SelectTrendChartVo;

public interface ProductTrendChartService {
    /**
     * 根据产品id和产品配置表id查询产品配置数据趋势图
     * @param sub
     * @param selectTrendChartVo
     * @return
     */
    Result<AnalysisDataVO> list(String sub, SelectTrendChartVo selectTrendChartVo);



}
