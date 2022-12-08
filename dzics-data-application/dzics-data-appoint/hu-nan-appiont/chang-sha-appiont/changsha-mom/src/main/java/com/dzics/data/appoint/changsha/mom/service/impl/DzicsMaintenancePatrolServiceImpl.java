package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.DzicsMaintenancePatrolDao;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsMaintenancePatrol;
import com.dzics.data.appoint.changsha.mom.model.vo.AddMainTenPatrolVo;
import com.dzics.data.appoint.changsha.mom.model.vo.EditMainTenPatrolVo;
import com.dzics.data.appoint.changsha.mom.service.DzicsMaintenancePatrolService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * <p>
 * 巡检维修表 服务实现类
 * </p>
 *
 * @author xnb
 * @since 2022-11-21
 */
@Service
public class DzicsMaintenancePatrolServiceImpl extends ServiceImpl<DzicsMaintenancePatrolDao, DzicsMaintenancePatrol> implements DzicsMaintenancePatrolService {

    @Autowired
    private DzicsMaintenancePatrolDao maintenancePatrolDao;

    @Override
    public Result addPatrol(AddMainTenPatrolVo addMainTenPatrolVo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        DzicsMaintenancePatrol dzicsMaintenancePatrol = new DzicsMaintenancePatrol();
        dzicsMaintenancePatrol.setModelType(addMainTenPatrolVo.getModelType());
        dzicsMaintenancePatrol.setOrderId(addMainTenPatrolVo.getOrderId());
        dzicsMaintenancePatrol.setOrderNo(addMainTenPatrolVo.getOrderNo());
        dzicsMaintenancePatrol.setType(Integer.valueOf(addMainTenPatrolVo.getType()));
        if("0".equals(addMainTenPatrolVo.getIntervalTime())){
            dzicsMaintenancePatrol.setIntervalTime(0);
        }else{
            dzicsMaintenancePatrol.setIntervalTime(addMainTenPatrolVo.getIntervalTime());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.DATE,+Integer.valueOf(addMainTenPatrolVo.getIntervalTime()));
            dzicsMaintenancePatrol.setNextExecuteData(simpleDateFormat.format(calendar.getTime()));
        }
        dzicsMaintenancePatrol.setExecuteData(simpleDateFormat.format(new Date()));
        dzicsMaintenancePatrol.setIsShow(0);
        dzicsMaintenancePatrol.setMessage(addMainTenPatrolVo.getMessage());
        dzicsMaintenancePatrol.setCreateTime(date);
        boolean save = this.save(dzicsMaintenancePatrol);
        if(save==true){
            return Result.ok();
        }
        return Result.error(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
    }

    @Override
    public Result editPatrol(EditMainTenPatrolVo editMainTenPatrolVo) {
        DzicsMaintenancePatrol dzicsMaintenancePatrol = this.getById(editMainTenPatrolVo.getId());
        if(dzicsMaintenancePatrol==null){
            return Result.error(CustomExceptionType.OK_NO_DATA,CustomResponseCode.ERR17);
        }
        dzicsMaintenancePatrol.setType(editMainTenPatrolVo.getType());
        dzicsMaintenancePatrol.setMessage(editMainTenPatrolVo.getMessage());
        dzicsMaintenancePatrol.setUpdateTime(new Date());
        boolean b = this.updateById(dzicsMaintenancePatrol);
        if(b==true){
            return Result.ok();
        }
        return Result.error(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
    }

    @Override
    public Result delPatrol(String Id) {
        if(StringUtils.isEmpty(Id)){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR,CustomResponseCode.ERR12);
        }
        DzicsMaintenancePatrol dzicsMaintenancePatrol = this.getById(Id);
        if(dzicsMaintenancePatrol==null){
            return Result.error(CustomExceptionType.OK_NO_DATA,CustomResponseCode.ERR11);
        }
        boolean b = this.removeById(Id);
        if(b==true){
            return Result.ok();
        }
        return Result.error("删除失败");
    }

    @Override
    public List<DzicsMaintenancePatrol> getPatrol(String type, String message,String modelType) {
        List<DzicsMaintenancePatrol> patrols = maintenancePatrolDao.getPatrol(type, message,modelType);
        if(!CollectionUtils.isEmpty(patrols)){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (DzicsMaintenancePatrol patrol : patrols) {
//                patrol.setIntervalTime(patrol.getIntervalTime()+"天每次");
                if(patrol.getExecuteData().equals(simpleDateFormat.format(new Date()))){
                    patrol.setIsShow(0);
                }
            }
        }
        return patrols;
    }

    @Override
    public Result putHealed(String id) throws ParseException {
        //参数是否为空
        if(StringUtils.isEmpty(id)){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR,CustomResponseCode.ERR12);
        }
        //数据是否存在
        DzicsMaintenancePatrol patrol = this.getById(id);
        if(patrol==null){
            throw new CustomException(CustomExceptionType.Parameter_Exception,CustomResponseCode.ERR11);
        }
        //校验是否到期
        if(LocalDate.parse(patrol.getExecuteData(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).compareTo(LocalDate.now())>0){
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR,CustomResponseCode.ERR0);
        }
        //设置新的处理日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,+Integer.valueOf(patrol.getIntervalTime()));
        patrol.setExecuteData(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        calendar.clear();
        calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(patrol.getExecuteData()));
        calendar.add(Calendar.DATE,+Integer.valueOf(patrol.getIntervalTime()));
        patrol.setNextExecuteData(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        patrol.setUpdateTime(new Date());
        patrol.setIsShow(1);
        boolean b = this.updateById(patrol);
        if(b==true){
            return Result.ok();
        }
        return Result.error("确认失败");
    }

    public static void main(String[] args) throws ParseException {
        Date date = new Date();
        String s1 = "2022-11-22";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date d1 = simpleDateFormat.parse(s1);
        String s2 = simpleDateFormat.format(date);
        if(s1.equals(s2)){
            System.out.println(true);
        }else{
            System.out.println(false);
        }
    }
}
