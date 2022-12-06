package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.ManageModeDao;
import com.dzics.data.appoint.changsha.mom.enums.ManageModeEnum;
import com.dzics.data.appoint.changsha.mom.model.entity.ManageMode;
import com.dzics.data.appoint.changsha.mom.service.ManageModeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author LiuDongFei
 * @date 2022年09月13日 15:36
 */
@Slf4j
@Service
public class ManageModeServiceImpl extends ServiceImpl<ManageModeDao, ManageMode> implements ManageModeService {


    @Override
    @Transactional
    public void manualOrder(String code, String type) {
        ManageMode one = this.getOne(Wrappers.<ManageMode>lambdaQuery().eq(ManageMode::getCode, code));
        one.setType(type);
        this.updateById(one);
    }

    @Override
    public boolean workNo() {
        ManageMode mode = this.getByCode(ManageModeEnum.WORK_NO.val());
        if (ObjectUtils.isEmpty(mode)) {
            ManageMode add = new ManageMode();
            add.setCode(ManageModeEnum.WORK_NO.val());
            add.setType("0");
            this.save(add);
            mode = add;
        }
        return "1".equals(mode.getType());
    }

    @Override
    public ManageMode getByCode(String code) {
        ManageMode one = this.getOne(Wrappers.<ManageMode>lambdaQuery()
                .eq(ManageMode::getCode, code));
        return one;
    }
}
