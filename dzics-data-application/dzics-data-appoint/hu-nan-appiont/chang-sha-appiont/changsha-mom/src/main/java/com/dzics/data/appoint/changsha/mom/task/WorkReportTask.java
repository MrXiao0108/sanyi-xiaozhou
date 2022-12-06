package com.dzics.data.appoint.changsha.mom.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dzics.data.appoint.changsha.mom.db.dao.MomWaitWorkReportDao;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitWorkReport;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingCjServiceImpl;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingJjServiceImpl;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingPgServiceImpl;
import com.dzics.data.common.base.model.dto.position.SendPosition;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname WorkReportTask
 * @Description 描述
 * @Date 2022/6/16 18:11
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class WorkReportTask {
    @Autowired
    private MomWaitWorkReportDao reportDao;
    @Autowired
    private WorkReportingCjServiceImpl cjService;
    @Autowired
    private WorkReportingJjServiceImpl jjService;
    @Autowired
    private WorkReportingPgServiceImpl pgService;

    /**
     * 监控MOM 报工失败的订单 重新报工
     * 没120秒执行一次
     */
    /*@Scheduled(initialDelay = 10000, fixedDelay = 120000)*/
    public void momOrderMonitor() {
        log.info("检测MOM报工失败的订单");
        PageHelper.startPage(1, 10);
        List<MomWaitWorkReport> orderIds = reportDao.selectList(Wrappers.emptyWrapper());
        PageInfo<MomWaitWorkReport> info = new PageInfo<>(orderIds);
        if (info.getTotal() > 0) {
            for (MomWaitWorkReport report : info.getList()) {
                try {
                    SendPosition sendPosition = new SendPosition();
                    BeanUtils.copyProperties(report, sendPosition);
                    String orderNo = report.getOrderNo();
                    Boolean s = null;
//            粗加工订单号
                    if (MomConstant.ORDER_DZ_1972.equals(orderNo) || MomConstant.ORDER_DZ_1973.equals(orderNo)) {
                        s = cjService.sendWorkReportingData(sendPosition, report.getId());
                    }
//            精加工订单号
                    if (MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)) {
                        s = jjService.sendWorkReportingData(sendPosition, report.getId());
                    }
//            抛光订单号
                    if (MomConstant.ORDER_DZ_1976.equals(orderNo)) {
                        s = pgService.sendWorkReportingData(sendPosition, report.getId());
                    }

                    if (Boolean.TRUE.equals(s)) {
                        reportDao.deleteById(report.getId());
                    }
                } catch (Throwable throwable) {
                    log.error("报工失败", throwable);
                }

            }

        }
    }
}
