package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.MomUser;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-10
 */
public interface MomUserService extends IService<MomUser> {

    /**
     * 根据工号 查询是否 运行插入
     *
     * @param employeeNo
     * @return true 允许插入 false 不允许插入
     */
    boolean isSaveUser(String employeeNo);

    Result getMomUser(PageLimitBase limit);

    MomUser getEmployeeNo(String employeeNo);

    @Cacheable(cacheNames = {"MomUserService.getLineIsLogin"}, key = "#orderCodeSys")
    MomUser getLineIsLogin(String orderNo, String lineNo, String orderCodeSys);

    @CacheEvict(cacheNames = {"MomUserService.getLineIsLogin"}, key = "#orderCodeSys")
    MomUser updateByIdCahce(MomUser momUser, String orderCodeSys);
}
