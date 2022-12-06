package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.service.OrderService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.pdm.db.dao.DzLineShiftDayDao;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.db.dao.DzOrderDao;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.pub.model.dto.AddOrderVo;
import com.dzics.data.pub.model.dto.DepartParms;
import com.dzics.data.pub.model.dto.OrderParmsModel;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.DzOrderDo;
import com.dzics.data.pub.model.vo.Orders;
import com.dzics.data.pub.model.vo.SelOrders;
import com.dzics.data.ums.db.dao.SysDepartDao;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author xnb
 * @date 2022年02月09日 15:58
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private DzicsUserService sysUserServiceDao;
    @Autowired
    private  SysDepartDao departDao;
    @Autowired
    private  DzOrderDao dzOrderMapper;
    @Autowired
    private   DzProductionLineDao dzProductionLineMapper;
    @Autowired
    private  DzEquipmentDao dzEquipmentMapper;
    @Autowired
    private   DzLineShiftDayDao dzLineShiftDayMapper;


    @Override
    public Result<DzOrder> add(String sub, AddOrderVo data) {
        SysUser sysUser = sysUserServiceDao.getByUserName(sub);
        String useDepartId = sysUser.getUseDepartId();
        String useOrgCode = sysUser.getUseOrgCode();
        List<DzOrder> orderNo = dzOrderMapper.selectList(new QueryWrapper<DzOrder>().eq("order_no", data.getOrderNo()));
        if (orderNo.size() > 0) {
            log.error("订单编号已存在:{}", data.getOrderNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_29);
        }

        DzOrder dzOrder = new DzOrder();
        if (useDepartId == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_39);
        }
        dzOrder.setDepartId(useDepartId);
        dzOrder.setOrderNo(data.getOrderNo());
        dzOrder.setRemarks(data.getRemarks());
        dzOrder.setOrgCode(useOrgCode);
        dzOrder.setCreateBy(sysUser.getRealname());
        int insert = dzOrderMapper.insert(dzOrder);
        if (insert > 0) {
            return new Result(CustomExceptionType.OK, dzOrder);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_1);
    }


    @Override
    public Result del(String sub, Long id) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        //判断订单是否绑定产线
        List<DzProductionLine> orderId = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>().eq("order_id", id));
        deleteLineIdByOrderNoLineNo();
        if (orderId.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_38);
        }
        QueryWrapper<DzOrder> eq = new QueryWrapper<DzOrder>().eq("id", id);
        if (byUserName.getUserIdentity().intValue() != UserIdentityEnum.DZ.getCode().intValue() && !byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            eq.eq("org_code", byUserName.getUseOrgCode());
        }
        DzOrder dzOrder = dzOrderMapper.selectOne(eq);
        if (dzOrder != null) {
            int i = dzOrderMapper.deleteById(id);
            return new Result(CustomExceptionType.OK, i);
        } else {
            log.error("数据不存在或机构编码不一致，id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_30);
        }

    }

    @Override
    public Result<List<DzOrderDo>> list(String sub, OrderParmsModel orderParmsModel) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        if (orderParmsModel.getPage() != -1) {
            PageHelper.startPage(orderParmsModel.getPage(), orderParmsModel.getLimit());
        }
        if (!StringUtils.isEmpty(orderParmsModel.getField())) {
            orderParmsModel.setField(UnderlineTool.humpToLine(orderParmsModel.getField()));
        }
        List<DzOrderDo> list = dzOrderMapper.listOrder(byUserName.getUseOrgCode(), orderParmsModel.getOrderNo(), byUserName.getUseDepartId(), orderParmsModel.getField(), orderParmsModel.getType());
        PageInfo<DzOrderDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public DzOrder selOrderNo(String orderNo) {
        QueryWrapper<DzOrder> wp = new QueryWrapper<>();
        wp.eq("order_no", orderNo);
        return dzOrderMapper.selectOne(wp);
    }

    @Transactional
    @Override
    public Result put(String sub, AddOrderVo data) {
        if (data.getId() == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        //订单编辑
        DzOrder dzOrder = dzOrderMapper.selectById(data.getId());
        if (dzOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        //判断订单编号是否有做更改
        if (!dzOrder.getOrderNo().equals(data.getOrderNo())) {
            //订单编号有做更改
            //1.判断新的订单编号是否重复
            List<DzOrder> orderNo = dzOrderMapper.selectList(new QueryWrapper<DzOrder>().eq("order_no", data.getOrderNo()));
            if (orderNo.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_29);
            }
            //2.新的订单号不存在 ，允许更改，更改所有与订单编号相关的表
            //2-1修改产线
            DzProductionLine dzProductionLine = new DzProductionLine();
            dzProductionLine.setOrderNo(data.getOrderNo());
            dzProductionLineMapper.update(dzProductionLine, new QueryWrapper<DzProductionLine>().eq("order_no", dzOrder.getOrderNo()));
            //2-2修改设备
            DzEquipment dzEquipment = new DzEquipment();
            dzEquipment.setOrderNo(data.getOrderNo());
            dzEquipmentMapper.update(dzEquipment, new QueryWrapper<DzEquipment>().eq("order_no", dzOrder.getOrderNo()));
            //2-3
            //修改设备每日排班表
            DzLineShiftDay dzLineShiftDay = new DzLineShiftDay();
            dzLineShiftDay.setOrderNo(data.getOrderNo());
            dzLineShiftDayMapper.update(dzLineShiftDay, new QueryWrapper<DzLineShiftDay>().eq("order_no", dzOrder.getOrderNo()));
        }


        dzOrder.setOrderNo(data.getOrderNo());
        dzOrder.setRemarks(data.getRemarks());
        dzOrderMapper.updateById(dzOrder);
        deleteLineIdByOrderNoLineNo();
        return new Result(CustomExceptionType.OK, dzOrder);
    }

    @Override
    public Result setlOrders(SelOrders selOrders, String sub) {
        List<Orders> orders = dzOrderMapper.selOrders(selOrders.getOrderId());
        return Result.ok(orders, Long.valueOf(orders.size()));
    }


    @Override
    public Result selOrdersDepart(DepartParms departParms, String sub) {
        List<Orders> orders = dzOrderMapper.selOrdersDepart(departParms.getDepartId());
        return Result.OK(orders);
    }

    @Override
    public void deleteLineIdByOrderNoLineNo() {}
}
