package com.dzics.data.boardms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.boardms.db.dao.SysDepartDao;
import com.dzics.data.boardms.db.dao.SysUserDao;
import com.dzics.data.boardms.model.entity.SysDepart;
import com.dzics.data.boardms.model.entity.SysUser;
import com.dzics.data.boardms.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import sun.misc.BASE64Encoder;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    @Autowired
    private SysDepartDao sysDepartDao;
    @Override
    public SysUser getByUserName(String sub) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>();
        queryWrapper.eq("username", sub);
        SysUser one = this.getOne(queryWrapper);
        if (one == null) {
            throw new CustomException(CustomExceptionType.USER_IS_NULL, CustomExceptionType.USER_IS_NULL.getTypeDesc() + ":" + sub);
        }
        SysDepart byId = sysDepartDao.selectById(one.getAffiliationDepartId());
        one.setCode(byId.getOrgCode());
        return one;
    }

}
