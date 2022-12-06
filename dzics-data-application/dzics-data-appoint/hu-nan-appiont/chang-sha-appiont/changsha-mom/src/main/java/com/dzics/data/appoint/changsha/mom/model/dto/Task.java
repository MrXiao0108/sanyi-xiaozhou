package com.dzics.data.appoint.changsha.mom.model.dto;

/**
 * Copyright 2021 bejson.com
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class Task implements Serializable {

    /**
     * WipOrderNo : 850026988960
     * WipOrderType : 1
     * ProductionLine : 5802XL01
     * WipOrderGroup : 32
     * GroupCount : 01
     * ProductNo : BCB006211877
     * ProductName : 右内侧板510C10SF.2.5.1-3
     * SerialNo : [""]
     * Facility : 5802
     * Quantity : 8
     * ScheduledStartDate : 2022-04-25 00:00:00
     * ScheduledCompleteDate : 2022-05-02 00:00:00
     * ProgressStatus : 110
     * WorkCenter : 105211
     * ProductAlias :
     */
    private String WipOrderNo;
    private String WipOrderType;
    private String ProductionLine;
    private String WipOrderGroup;
    private String GroupCount;
    private String ProductNo;
    private String ProductName;
    private String Facility;
    private int Quantity;
    private String ScheduledStartDate;
    private String ScheduledCompleteDate;
    private String ProgressStatus;
    private String WorkCenter;
    private String ProductAlias;
    private String paramRsrv1;
    private String paramRsrv2;
    private List<String> SerialNo;
}
