package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.LockScreenPassWord;
import com.dzics.data.pub.model.entity.SysConfig;
import com.dzics.data.pub.model.vo.RunDataModel;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 系统运行模式 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-31
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 更新锁屏密码
     * @param sub
     * @param screenPassWord
     * @return
     */
    Result putLockScreenPassword(String sub, LockScreenPassWord screenPassWord);

    /**
     * 获取看板锁屏密码
     *
     *
     * @param screenPassWord
     * @param sub
     * @return
     */
    Result getLockScreenPassword(LockScreenPassWord screenPassWord, String sub);

    /**
     * 获取系统运行模式
     *
     * @param sub
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.systemRunModel", key = "'runModel'")
    Result systemRunModel(String sub);

    /**
     * 修改系统运行模式
     *
     * @param sub
     * @param runDataModel
     * @return
     */
    @CachePut(cacheNames = "dzDetectionTemplCache.systemRunModel", key = "'runModel'")
    Result editSystemRunModel(String sub, RunDataModel runDataModel);



    RunDataModel systemRunModel();

    void editSystemRunModel(RunDataModel runDataModel);

    SysConfig getConfig(String i);

    void updateConfigType(String lockPassword);

    List<String> getMouthDate(int year, int monthValue);
}
