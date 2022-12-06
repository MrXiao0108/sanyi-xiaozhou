package com.dzics.data.appoint.changsha.mom.model.dto.mom;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * MOM将缓存区库存下发给中控，中控根据库存进行叫料
 */
@Data
public class MOMStorageDto implements Serializable {

    /**
     * 工厂
     */
    private String Facility;

    /**
     * 产线
     */
    private String ProductionLine;

    /**
     * 工作中心
     */
    private String WorkCenter;

    /**
     * 工作中心描述
     */
    private String WorkCenterName;

    /**
     * 预留参数1
     */
    private String paramRsrv1;

    /**
     * 预留参数2
     */
    private String paramRsrv2;

    /*
     * 物料库存清单
     */
    private List<Material> MaterialList;

    @Data
    public static class Material {

        /**
         * 订单号
         */
        private String WipOrderNo;

        /**
         * 产品物料号
         */
        private String ProductNo;

        /**
         * 数量
         */
        private BigDecimal Quantity;

        /**
         * 顺序号
         */
        private String SequenceNo;

        /**
         * 工序号
         */
        private String OprSequenceNo;

        /**
         * 工序名称
         */
        private String OprSequenceName;

        /**
         * 预留参数1
         */
        private String paramRsrv1;

        /**
         * 预留参数2
         */
        private String paramRsrv2;
    }
}
