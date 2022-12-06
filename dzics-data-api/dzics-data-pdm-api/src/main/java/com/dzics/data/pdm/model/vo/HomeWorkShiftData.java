package com.dzics.data.pdm.model.vo;

import com.dzics.data.common.base.model.vo.kanban.WorkShiftSum;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/9/27.
 * @since
 */
@Data
public class HomeWorkShiftData<T> {
    private List<WorkShiftSum> dayWorkShiftSum;
    private T mouthWorkShiftSum;
    private String mouthValue;
}
