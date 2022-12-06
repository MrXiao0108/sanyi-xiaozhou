package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import org.springframework.http.ResponseEntity;

/**
 * 中控集成DNC进行产线换型
 *
 * @author liudongfei
 */
public interface DncReportingService {

    /**
     * 下载程序接口
     *
     * @param order: MOM订单实体
     * @return DNC响应
     * @author van
     * @date 2022/6/28
     */
    ResponseEntity<Object> downloadProgram(MomOrder order);
}
