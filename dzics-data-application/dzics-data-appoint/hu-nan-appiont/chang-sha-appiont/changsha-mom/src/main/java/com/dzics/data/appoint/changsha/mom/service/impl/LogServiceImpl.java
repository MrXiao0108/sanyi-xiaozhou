package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.db.dao.DzicsInsideLogDao;
import com.dzics.data.appoint.changsha.mom.db.dao.MomCommunicationLogDao;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import com.dzics.data.appoint.changsha.mom.model.entity.MomCommunicationLog;
import com.dzics.data.appoint.changsha.mom.model.respon.DzicsInsidDo;
import com.dzics.data.appoint.changsha.mom.model.respon.IndexLogDo;
import com.dzics.data.appoint.changsha.mom.model.respon.MomTcpDo;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.LogService;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author xnb
 * @date 2022/11/14 0014 10:34
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private MomCommunicationLogDao communicationLogDao;
    @Autowired
    private DzicsInsideLogDao insideLogDao;
    @Autowired
    private CachingApi cachingApi;

    @Override
    public IndexLogDo getMomIndexBackLog() {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        IndexLogDo indexLogDo = new IndexLogDo();
        //mom
        PageHelper.startPage(1,20);
        List<MomCommunicationLog> communicationLogs = communicationLogDao.selectList(new QueryWrapper<MomCommunicationLog>().orderByDesc("create_time"));
        PageInfo pageInfo1 = new PageInfo<>(communicationLogs);
        MomTcpDo momTcpDo = new MomTcpDo();
        List<MomCommunicationLog>list1 = pageInfo1.getList();
        if(!CollectionUtils.isEmpty(list1)){
            for (MomCommunicationLog communicationLog : list1) {
                if("101".equals(communicationLog.getBusinessType())){
                    communicationLog.setBusinessType("叫料");
                }else if("102".equals(communicationLog.getBusinessType())){
                    communicationLog.setBusinessType("叫空框");
                }else if("103".equals(communicationLog.getBusinessType())){
                    communicationLog.setBusinessType("满料拖出");
                }else if("104".equals(communicationLog.getBusinessType())){
                    communicationLog.setBusinessType("空框拖出");
                }else if("105".equals(communicationLog.getBusinessType())){
                    communicationLog.setBusinessType("报工");
                }else if("106".equals(communicationLog.getBusinessType())){
                    communicationLog.setBusinessType("查询料框信息");
                }

                if("1".equals(communicationLog.getResultState())){
                    communicationLog.setResultState("失败");
                }else{
                    communicationLog.setResultState("成功");
                }
            }
        }
        momTcpDo.setList(list1);
        indexLogDo.setMomTcpDo(momTcpDo);

        //dzics
        PageHelper.startPage(1,20);
        List<DzicsInsideLog> dzicsInsideLogs = insideLogDao.selectList(new QueryWrapper<DzicsInsideLog>().eq("line_id",line.getId()).orderByDesc("create_time"));
        PageInfo pageInfo2 = new PageInfo<>(dzicsInsideLogs);
        DzicsInsidDo insidDo = new DzicsInsidDo();
        List<DzicsInsideLog>list2 = pageInfo2.getList();
        if(!CollectionUtils.isEmpty(list2)){
            for (DzicsInsideLog insideLog : list2) {
                if("0".equals(insideLog.getState())){
                    insideLog.setState("成功");
                }else{
                    insideLog.setState("失败");
                }
                if("1".equals(insideLog.getBusinessType())){
                    insideLog.setBusinessType("Mom订单下发");
                }else if("4".equals(insideLog.getBusinessType())){
                    insideLog.setBusinessType("生产叫料");
                }else if("17".equals(insideLog.getBusinessType())){
                    insideLog.setBusinessType("Mom订单工序下发");
                }else if("103".equals(insideLog.getBusinessType())){
                    insideLog.setBusinessType("满料拖出");
                }
            }
        }
        insidDo.setList(list2);
        indexLogDo.setDzicsInsidDo(insidDo);
        return indexLogDo;
    }
}
