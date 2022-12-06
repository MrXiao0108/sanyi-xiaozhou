package com.dzics.data.pdm.service.kanban;

import java.time.LocalDate;
import java.util.List;

/**
 * @Classname DeviceProNumService
 * @Description 描述
 * @Date 2022/3/21 11:52
 * @Created by NeverEnd
 */
public interface DeviceProNumService<T> {
    T getEqIdData(LocalDate now, List<String> collect, String tableKey, String orderNo);
}
