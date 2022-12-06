package com.dzics.data.pdm.db.model.dao;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DayDataResultDo {
    private String date;
    private BigDecimal badnessNum;
    private BigDecimal qualifiedNum;
}
