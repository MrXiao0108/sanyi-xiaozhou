package com.dzics.data.common.base.model.vo.kanban;

import lombok.Data;

import java.util.List;

@Data
public class GetOutputByLineId2Do {

    private List<Long> dateList;
    List<String> dayList;
    private List<Long> ng;
}
