package com.dzics.data.business.service;


import com.dzics.data.business.model.vo.cpk.AnalysisDataVO;

/**
 * cpk 数据分析接口
 *
 * @author ZhangChengJun
 * Date 2021/6/28.
 * @since
 */
public interface CpkService {

    /**
     * cpk 数据分析
     *
     * @param doubles
     * @return
     */
    AnalysisDataVO analysisData(double[] doubles , double standardValue, double upperValue, double lowerValue);
}
