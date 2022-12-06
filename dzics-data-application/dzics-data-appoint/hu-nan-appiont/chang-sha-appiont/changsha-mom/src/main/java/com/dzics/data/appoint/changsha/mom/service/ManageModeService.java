package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.ManageMode;
import com.dzics.data.common.base.vo.Result;

import javax.swing.text.View;


public interface ManageModeService extends IService<ManageMode> {


    void manualOrder(String code, String type);

    /**
     * 报工时是否需要发送序列号
     *
     * @return true：发送；false：不发送
     */
    boolean workNo();

    /**
     * 通过code查询实体
     *
     * @param code 编号
     * @return 查询结果
     */
    ManageMode getByCode(String code);
}
