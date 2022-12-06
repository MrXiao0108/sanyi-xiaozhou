package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.ConfigType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.md5.Md5Util;
import com.dzics.data.pub.db.dao.SysConfigDao;
import com.dzics.data.pub.model.dto.LockScreenPassWord;
import com.dzics.data.pub.model.entity.SysConfig;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统运行模式 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-31
 */
@Service
@SuppressWarnings(value = "ALL")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfig> implements SysConfigService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result putLockScreenPassword(String sub, LockScreenPassWord screenPassWord) {
        String pass = Md5Util.md5(screenPassWord.getLockPassword());
        updateConfigType(pass);
        return Result.ok();
    }

    @Override
    public Result getLockScreenPassword(LockScreenPassWord screenPassWord, String sub) {
        SysConfig sysConfig = getConfig(ConfigType.ConfigPassword);
        String lockPasswordX = screenPassWord.getLockPassword();
        String pass = Md5Util.md5(lockPasswordX);
        if (pass.equals(sysConfig.getConfigValue())) {
            return Result.ok();
        }
        return Result.error(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, CustomResponseCode.ERR47);
    }

    @Override
    public Result editSystemRunModel(String sub, RunDataModel runDataModel) {
        Object keySize = redisUtil.get(RedisKey.KEY_RUN_MODEL_DANGER);
        if (keySize == null) {
            redisUtil.set(RedisKey.KEY_RUN_MODEL_DANGER, Integer.valueOf(1), 5);
            return Result.ok(runDataModel);
        }
        if (keySize != null) {
            Integer ketSi = (Integer) keySize;
            if (ketSi.intValue() < 5) {
                ketSi = ketSi.intValue() + 1;
                redisUtil.set(RedisKey.KEY_RUN_MODEL_DANGER, ketSi, 5);
                return Result.ok(runDataModel);
            } else {
                redisUtil.del(RedisKey.KEY_RUN_MODEL_DANGER);
            }
        }
        if ("dz_equipment_pro_num".equals(runDataModel.getTableName())) {
            runDataModel.setTableName("dz_equipment_pro_num_signal");
            runDataModel.setPlanDay("dz_production_plan_day_signal");
            runDataModel.setRunDataModel("脉冲模式");
        } else {
            runDataModel.setTableName("dz_equipment_pro_num");
            runDataModel.setPlanDay("dz_production_plan_day");
            runDataModel.setRunDataModel("数量累计模式");
        }
        editSystemRunModel(runDataModel);
        return Result.ok(runDataModel);
    }

    @Override
    public Result systemRunModel(String sub) {
        RunDataModel runDataModel = systemRunModel();
        if (runDataModel == null) {
            runDataModel.setTableName("dz_equipment_pro_num");
            runDataModel.setPlanDay("dz_production_plan_day");
            runDataModel.setRunDataModel("数量累计模式");
        }
        return Result.OK(runDataModel);
    }

    @Override
    public RunDataModel systemRunModel() {
        return baseMapper.systemRunModel(ConfigType.rumModel);
    }

    @Override
    public void editSystemRunModel(RunDataModel runDataModel) {
        QueryWrapper<SysConfig> wp = new QueryWrapper<>();
        wp.eq("type_config", ConfigType.rumModel);
        SysConfig sysConfig = new SysConfig();
        sysConfig.setPlanday(runDataModel.getPlanDay());
        sysConfig.setRundatamodel(runDataModel.getRunDataModel());
        sysConfig.setTablename(runDataModel.getTableName());
        baseMapper.update(sysConfig, wp);
    }

    @Override
    public SysConfig getConfig(String i) {
        QueryWrapper<SysConfig> wp = new QueryWrapper<>();
        wp.eq("type_config", i);
        return baseMapper.selectOne(wp);
    }

    @Override
    public void updateConfigType(String lockPassword) {
        QueryWrapper<SysConfig> wp = new QueryWrapper<>();
        wp.eq("type_config", ConfigType.ConfigPassword);
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigValue(lockPassword);
        baseMapper.update(sysConfig, wp);
    }

    @Override
    public List<String> getMouthDate(int year, int monthValue) {
       // List list = redisUtil.lGet(RedisKey.SysConfigServiceImpl + year + monthValue, 0, -1);
       // if (CollectionUtils.isNotEmpty(list)) {
         //   return list;
       // } else {
            List<String> mouthDate = baseMapper.getMouthDate(year, monthValue);
          //  redisUtil.lSet(RedisKey.SysConfigServiceImpl + year + monthValue, mouthDate, 3601 * 24);
            return mouthDate;
        }
   }

