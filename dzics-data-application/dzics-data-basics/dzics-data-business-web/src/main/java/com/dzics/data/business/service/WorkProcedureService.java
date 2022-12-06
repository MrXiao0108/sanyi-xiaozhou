package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.WorkingProcedureAdd;

public interface WorkProcedureService {

    /**
     * 编辑工序
     *
     * @param procedureAdd
     * @param sub
     * @return
     */
    Result editWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub);
    /**
     * 新增工序
     *
     * @param procedureAdd 工序编号 名称 订单 产线 id
     * @param sub
     * @return
     */
    Result addWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub);
}
