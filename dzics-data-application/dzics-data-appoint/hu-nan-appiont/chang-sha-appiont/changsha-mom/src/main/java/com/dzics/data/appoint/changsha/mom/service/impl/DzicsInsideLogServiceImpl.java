package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.DzicsInsideLogDao;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import com.dzics.data.appoint.changsha.mom.model.vo.DzicsInsideVo;
import com.dzics.data.appoint.changsha.mom.service.DzicsInsideLogService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xnb
 * @since 2022-11-12
 */
@Service
public class DzicsInsideLogServiceImpl extends ServiceImpl<DzicsInsideLogDao, DzicsInsideLog> implements DzicsInsideLogService {

    @Autowired
    private DzicsInsideLogDao insideLogDao;

    @Override
    public List<DzicsInsideLog> getBackInsideLog(DzicsInsideVo insideVo) throws ParseException {
        if(StringUtils.isEmpty(insideVo.getBeginTime()) && StringUtils.isEmpty(insideVo.getEndTime())){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            insideVo.setEndTime(simpleDateFormat.format(new Date()));
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(insideVo.getEndTime()));
            calendar.add(Calendar.DATE,-7);
            insideVo.setBeginTime(simpleDateFormat.format(calendar.getTime()));
        }
        if(insideVo.getPage() != -1){
            PageHelper.startPage(insideVo.getPage(),insideVo.getLimit());
        }
        List<DzicsInsideLog> insideLogs = insideLogDao.getBackInsideLogs(insideVo.getBeginTime(), insideVo.getEndTime(), insideVo.getBusinessType(), insideVo.getState(), insideVo.getThrowMsg(), insideVo.getField(), insideVo.getType());
        return insideLogs;
    }
}
