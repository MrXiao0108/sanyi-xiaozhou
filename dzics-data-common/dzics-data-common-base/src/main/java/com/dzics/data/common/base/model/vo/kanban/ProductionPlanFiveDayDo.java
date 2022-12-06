package com.dzics.data.common.base.model.vo.kanban;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductionPlanFiveDayDo {

    List<BigDecimal> list;
    List<String>dateList;
}
