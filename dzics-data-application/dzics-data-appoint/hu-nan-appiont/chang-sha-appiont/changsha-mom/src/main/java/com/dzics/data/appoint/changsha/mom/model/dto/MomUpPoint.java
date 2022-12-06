package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;

/**
 * 小车对应上料点
 *
 * @author ZhangChengJun
 * Date 2021/11/8.
 * @since
 */
@Data
public class MomUpPoint {

    /**
     * 工位编号
     */
    private String stationCode;

    /**
     * 上料点编码
     */
    private String externalCode;

    /**
     * 料点模式
     * 料点模式, NG （NG物料） TL (退库) ,SL (上料)
     */
    private String pointModel;

    /**
     * 料点料框类型
     */
    private String palletType;
}
