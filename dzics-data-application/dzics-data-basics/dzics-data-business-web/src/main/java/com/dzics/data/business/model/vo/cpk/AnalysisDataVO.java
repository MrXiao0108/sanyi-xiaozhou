package com.dzics.data.business.model.vo.cpk;

import lombok.Data;

@Data
public class AnalysisDataVO {
    private double[] analysisData;
    private Double standValue;
    private Double lowerLimitValue;
    private Double upperLimitValue;
    private CPK cpk;

}
