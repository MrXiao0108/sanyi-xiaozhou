package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.WorkingProcedureAdd;
import com.dzics.data.pub.model.entity.DzWorkingProcedure;
import com.dzics.data.pub.model.vo.WorkingProcedureRes;

import java.util.List;

/**
 * <p>
 * 工序表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
public interface DzWorkingProcedureService extends IService<DzWorkingProcedure> {

    /**
     * 查询工列表
     *
     * @param pageLimit
     * @param procedureAdd
     * @param useDepartId
     * @return
     */
    Result<List<WorkingProcedureRes>> selWorkingProcedure(PageLimit pageLimit, WorkingProcedureAdd procedureAdd, String useDepartId);





    /**
     * 删除工序
     *
     * @param id
     * @param sub
     * @return
     */
    Result delWorkingProcedure(String id, String sub);
}
