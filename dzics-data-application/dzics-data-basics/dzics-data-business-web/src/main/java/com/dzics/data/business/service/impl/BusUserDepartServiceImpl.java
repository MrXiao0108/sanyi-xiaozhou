package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.service.BusUserDepartService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pub.db.dao.DzOrderDao;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.ums.db.dao.*;
import com.dzics.data.ums.model.entity.*;
import com.dzics.data.ums.model.vo.depart.ResDepart;
import com.dzics.data.ums.service.SysRolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Classname BusUserDepartServiceImpl
 * @Description 描述
 * @Date 2022/3/9 18:16
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class BusUserDepartServiceImpl implements BusUserDepartService {

    @Autowired
    private SysDepartDao sysDepartDao;
    @Autowired
    private SysDepartPermissionDao departPermissionDao;
    @Autowired
    private SysUserDao userDao;
    @Autowired
    private SysRoleDao roleDao;
    @Autowired
    private SysUserDepartDao userDepartDao;
    @Autowired
    private SysRolePermissionService rolePermissionService;
    @Autowired
    private DzProductDao productDao;
    @Autowired
    private DzOrderDao orderDo;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result<ResDepart> delDepart(String departId, String sub) {
        List<DzProduct> dzProducts = productDao.selectList(new QueryWrapper<DzProduct>().eq("depart_id", departId));
        if (dzProducts.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_69);
        }
        List<DzOrder> order = orderDo.selectList(new QueryWrapper<DzOrder>().eq("depart_id", departId));
        if (order.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_70);
        }
        SysDepart byId = sysDepartDao.selectById(departId);
        Integer sizeUser = userDao.getCountByOrgCode(byId.getOrgCode());
        if (sizeUser.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR25);
        }
        sysDepartDao.deleteById(departId);

        QueryWrapper<SysDepartPermission> depMissWp = new QueryWrapper<>();
        depMissWp.eq("depart_id", departId);
        departPermissionDao.delete(depMissWp);
        QueryWrapper<SysUserDepart> wpUserDep = new QueryWrapper<>();
        wpUserDep.eq("depart_id", departId);
        userDepartDao.delete(wpUserDep);
        QueryWrapper<SysRole> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("org_code", byId.getOrgCode());
        roleDao.delete(roleQueryWrapper);
        QueryWrapper<SysRolePermission> wpRoPermis = new QueryWrapper<>();
        wpRoPermis.eq("org_code", byId.getOrgCode());
        rolePermissionService.remove(wpRoPermis);
        return new Result(CustomExceptionType.OK);
    }
}
