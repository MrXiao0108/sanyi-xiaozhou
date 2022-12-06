package com.dzics.data.boardms.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.boardms.model.dto.*;
import com.dzics.data.boardms.model.entity.SysInterfaceMethod;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * 接口方法配置
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
public interface InterfaceMethod extends IService<SysInterfaceMethod> {
    /**
     * 获取方法接口方法列表
     *
     * @param sub
     * @param pageLimit
     * @return
     */
    Result getInterfaceMethod(String sub, PageLimit pageLimit);

    /**
     * 新增接口
     *
     * @param sub
     * @param interfaceMethod
     * @return
     */
    Result addInterfaceMethod(String sub, InterfaceMethodParm interfaceMethod);

    /**
     * 编辑接口信息
     *
     * @param sub
     * @param sysInterfaceMethod
     * @return
     */
    @CacheEvict(cacheNames = {"interfaceMethod.getMethodGroup"},allEntries = true)
    Result editInterfaceMethod(String sub, InterfaceMethodParm sysInterfaceMethod);

    /**
     * 组列表
     *
     * @param sub
     * @param pageLimit
     * @return
     */
    Result getGroup(String sub, PageLimit pageLimit);

    /**
     * 新增组
     *
     * @param sub
     * @param interfaceGroup
     * @return
     */
    Result addGroup(String sub, InterfaceGroupParm interfaceGroup);

    /**
     * 编辑组
     *
     * @param sub
     * @param interfaceGroup
     * @return
     */
    @CacheEvict(cacheNames = {"interfaceMethod.getMethodGroup"},allEntries = true)
    Result editGroup(String sub, InterfaceGroupParm interfaceGroup);

    /**
     * 设置接口组
     *
     * @param sub
     * @param groupConfiguration
     * @return
     */
    @CacheEvict(cacheNames = {"interfaceMethod.getMethodGroup"},allEntries = true)
    Result addGroupInerfaceConfig(String sub, InGrConfiguration groupConfiguration);

    /**
     * 查询组配置接口
     *
     * @param sub
     * @param groupConfiguration
     * @return
     */
    Result getGroupInerfaceConfig(String sub, GroupConfigParm groupConfiguration);

    /**
     * 根据组名称获取接口列表
     *
     * @param methodGroup 接口组唯一编码
     * @return
     */
    @Cacheable(cacheNames = {"interfaceMethod.getMethodGroup"}, key = "#methodGroup",unless = "#result == null")
    Result getMethodGroup(String methodGroup);

    /**
     * 根据接口id删除
     * @param sub
     * @param delInterfaceMethod
     * @return
     */
    Result delInterfaceMethod(String sub, DelInterfaceMethod delInterfaceMethod);

    /**
     * 删除组接口
     * @param sub
     * @param interfaceGroup
     * @return
     */
    Result delGroup(String sub, DelInterfaceGroup interfaceGroup);
}
