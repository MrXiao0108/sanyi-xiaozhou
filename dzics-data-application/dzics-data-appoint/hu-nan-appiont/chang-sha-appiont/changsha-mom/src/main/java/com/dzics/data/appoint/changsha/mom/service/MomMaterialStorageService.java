package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.MOMStorageDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialStorage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 物料仓库 服务类
 * </p>
 *
 * @author van
 * @since 2022-07-27
 */
public interface MomMaterialStorageService extends IService<MomMaterialStorage> {

    /*
     * MOM库存下发处理
     */
    ResultDto storageHandle(IssueOrderInformation<MOMStorageDto> information);


    /**
     * 订单物料库存
     *
     * @param c: wipOrderNo collection
     * @return 查询结果
     * @author van
     * @date 2022/8/13
     */
    Map<String, String> wipOrderNoTotal(Collection<String> c);
}
