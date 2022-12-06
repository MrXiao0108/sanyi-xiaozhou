package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.ums.model.vo.depart.ResDepart;
import org.springframework.cache.annotation.CacheEvict;

/**
 * @Classname BusUserDepartService
 * @Description 站点接口
 * @Date 2022/3/9 18:16
 * @Created by NeverEnd
 */
public interface BusUserDepartService {

    /**
     * 删除站点
     *
     * @param departId 站点id
     * @param sub      操作用户
     *                1 清除站点id查询信息
     *                2 清除站点编码存储信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessDepartService.getById"
            , "departService.getByCode","rolePermissionService.listRolePermission",
            "businessDepartService.listAll","businessUserService.querySwitchSite"}, allEntries = true)
    Result<ResDepart> delDepart(String departId, String sub);
}
