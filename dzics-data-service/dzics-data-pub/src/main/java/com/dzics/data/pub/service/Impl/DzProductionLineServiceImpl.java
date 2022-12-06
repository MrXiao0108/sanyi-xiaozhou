package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.pub.db.model.dto.LineListDo;
import com.dzics.data.pub.db.model.dto.Lines;
import com.dzics.data.pub.model.dto.BingEquipmentVo;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import com.dzics.data.pub.model.vo.SelOrders;
import com.dzics.data.pub.service.DzProductionLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 * 产线表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
@Slf4j
public class DzProductionLineServiceImpl extends ServiceImpl<DzProductionLineDao, DzProductionLine> implements DzProductionLineService {

    @Autowired
    private DzProductionLineDao lineDao;
    @Autowired
    private DzProductionLineService lineService;

    @Override
    public OrderIdLineId getOrderNoLineNoId(String orderCode, String lineNo) {
        return getOrderNoAndLineNo(orderCode, lineNo);
    }

    @Override
    public DzProductionLine getLineIdByOrderNoLineNo(GetOrderNoLineNo getOrderNoLineNo) {
        QueryWrapper<DzProductionLine> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", getOrderNoLineNo.getOrderNo());
        wrapper.eq("line_no", getOrderNoLineNo.getLineNo());
        DzProductionLine dzProductionLine = lineDao.selectOne(wrapper);
        return dzProductionLine;
    }

    @Override
    public String getOnelineNo(String productionLineNumber) {
        QueryWrapper<DzProductionLine> wpProLine = new QueryWrapper<>();
        wpProLine.select("id");
        wpProLine.eq("line_no", productionLineNumber);
        DzProductionLine dzProductionLine = lineDao.selectOne(wpProLine);
        return dzProductionLine != null ? dzProductionLine.getId() : null;
    }

    @Override
    public OrderIdLineId getOrderNoAndLineNo(String orderCode, String lineNo) {
        QueryWrapper<DzProductionLine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderCode);
        queryWrapper.eq("line_no", lineNo);
        DzProductionLine one = lineService.getOne(queryWrapper);
        if (!ObjectUtils.isEmpty(one)) {
            OrderIdLineId orderIdLineId = new OrderIdLineId();
            orderIdLineId.setLineId(one.getId());
            orderIdLineId.setOrderId(one.getOrderId());
            orderIdLineId.setLienNo(one.getLineNo());
            orderIdLineId.setOrderNo(one.getOrderNo());
            return orderIdLineId;
        }
        return null;
    }

    @Override
    public Result putStatus(String sub, Long id) {
        DzProductionLine dzProductionLine = lineDao.selectById(id);
        if (dzProductionLine == null) {//
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_28);
        }
        if (dzProductionLine.getStatus() == 0) {
            //当前为禁用  改为启用
            dzProductionLine.setStatus(1);
        } else if (dzProductionLine.getStatus() == 1) {
            //当前为启用，改为禁用
            dzProductionLine.setStatus(0);
        }
        lineDao.updateById(dzProductionLine);
        return new Result(CustomExceptionType.OK, dzProductionLine);
    }


    @Override
    public Result bingEquipment(String sub, BingEquipmentVo bingEquipmentVo) {
        DzProductionLine dzProductionLine = lineDao.selectById(bingEquipmentVo.getLineId());
        dzProductionLine.setStatisticsEquimentId(bingEquipmentVo.getEquipmentId());
        lineDao.updateById(dzProductionLine);
        return new Result(CustomExceptionType.OK, dzProductionLine);
    }

    @Override
    public Result allLineList(String useOrgCode) {
        List<LineListDo> list = lineDao.allLineList(useOrgCode);
        return new Result(CustomExceptionType.OK, list, list.size());
    }


    @Override
    public Result getByOrderIdV2(String sub, Long ordeId) {
        List<Lines> proLines = lineDao.getByOerderId(ordeId);
        return Result.ok(proLines);
    }

    @Override
    public DzProductionLine getLineId(String lineId) {
        return lineDao.selectById(lineId);
    }

    @Override
    public void deleteLineIdByOrderNoLineNo() {
    }

    @Override
    public Result selLines(SelOrders selOrders, String useOrgCode) {
        List<Lines> lines = lineDao.listOrderId(selOrders.getOrderId(), useOrgCode);
        return Result.ok(lines, Long.valueOf(lines.size()));
    }

}
