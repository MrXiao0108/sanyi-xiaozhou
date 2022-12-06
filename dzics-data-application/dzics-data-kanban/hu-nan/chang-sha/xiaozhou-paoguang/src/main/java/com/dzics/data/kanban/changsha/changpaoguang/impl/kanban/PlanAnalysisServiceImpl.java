package com.dzics.data.kanban.changsha.changpaoguang.impl.kanban;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.vo.kanban.PlanAnalysisDoSingle;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pdm.db.dao.DzLineShiftDayDao;
import com.dzics.data.pdm.db.dao.DzProductionPlanDao;
import com.dzics.data.pdm.db.dao.DzProductionPlanDayDao;
import com.dzics.data.pdm.model.entity.DzEquipmentProNum;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.model.entity.DzProductionPlan;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.service.kanban.PlanShiftService;
import com.dzics.data.pub.service.kanban.TaktTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Slf4j
@Service
public class PlanAnalysisServiceImpl implements PlanShiftService {

    @Autowired
    public DzProductionPlanDayDao planDayDao;
    @Autowired
    public DzEquipmentDao equipmentDao;
    @Autowired
    public DzLineShiftDayDao shiftDayDao;
    @Autowired
    public DzEquipmentProNumDao equipmentProNumDao;
    @Autowired
    public TaktTimeService taktTimeService;
    @Autowired
    public DzProductionPlanDao productionPlanDao;

    @Override
    public Result getWorkShiftCapacity(GetOrderNoLineNo orderNoLineNo) throws Exception {
        //定义返回前端entity
        PlanAnalysisDoSingle planAnalysisDoSingle = new PlanAnalysisDoSingle();

        //抛光只有一台机器人，直接获取设备表机器人，无需判断后台绑定哪台设备
        DzEquipment equipment = equipmentDao.selectOne(new QueryWrapper<DzEquipment>()
                        .eq("order_no",orderNoLineNo.getOrderNo())
                        .eq("line_no",orderNoLineNo.getLineNo())
                        .eq("equipment_type", 3));
        if(equipment==null){
            return new Result(CustomExceptionType.OK_NO_DATA, Message.ERR_56);
        }
        //获取当前时间，判断当前所处班次
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String workName = "";
        //属于昨日晚班
        if(calendar.getTime().getHours() < 8){
            workName="晚班";
            date = new Date(System.currentTimeMillis()-24*60*60*1000);
        }
        //属于当日白班
        if(calendar.getTime().getHours() >= 8 && calendar.getTime().getHours() < 20){
            workName="早班";
            date = new Date();
        }
        //属于当日晚班
        if(calendar.getTime().getHours() >= 20){
            workName="晚班";
            date = new Date();
        }
        String format = sdf.format(date);
        //获取排班
        DzLineShiftDay lineShiftDay = shiftDayDao.selectOne(new QueryWrapper<DzLineShiftDay>()
                .eq("order_no",orderNoLineNo.getOrderNo()).eq("line_no",orderNoLineNo.getLineNo())
                .eq("work_data",format)
                .eq("work_name",workName)
                .eq("eq_id",equipment.getId()));

        if(lineShiftDay==null){
            return Result.error(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR17);
        }

        //获取设备每日生产数据
        DzEquipmentProNum proNum = equipmentProNumDao.selectOne(new QueryWrapper<DzEquipmentProNum>()
                .eq("order_no",orderNoLineNo.getOrderNo())
                .eq("line_no",orderNoLineNo.getLineNo())
                .eq("equiment_id",equipment.getId())
                .eq("day_id", lineShiftDay.getId()));
        if(proNum==null){return Result.error(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR17);}
        //生产节拍
        BigDecimal taktTime = (BigDecimal)taktTimeService.getTaktTime(orderNoLineNo);
        //合格率
        long qualifNum = proNum.getQualifiedNum() / proNum.getNowNum();
        //获取产线计划
        DzProductionPlan dzProductionPlan = productionPlanDao.selectOne(new QueryWrapper<DzProductionPlan>()
                .eq("line_id", lineShiftDay.getLineId())
                .eq("plan_type", 0)
                .eq("status", 1)
                .eq("del_flag", 0));
        //达成率
        long complete = proNum.getNowNum() / dzProductionPlan.getPlannedQuantity() * 100;
        planAnalysisDoSingle.setPercentageComplete(BigDecimal.valueOf(complete));
        planAnalysisDoSingle.setTaktTime(taktTime);
        planAnalysisDoSingle.setPassRate(BigDecimal.valueOf(qualifNum));
        return new Result(CustomExceptionType.OK,planAnalysisDoSingle);
    }

}
