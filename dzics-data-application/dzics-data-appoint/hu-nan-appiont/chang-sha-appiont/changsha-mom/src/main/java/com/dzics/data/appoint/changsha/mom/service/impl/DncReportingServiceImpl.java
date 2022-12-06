package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSON;
import com.dzics.data.appoint.changsha.mom.config.DncRequestPath;
import com.dzics.data.appoint.changsha.mom.model.dto.DownloadParameter;
import com.dzics.data.appoint.changsha.mom.model.dto.dnc.DNCDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.service.DncReportingService;
import com.dzics.data.appoint.changsha.mom.util.RedisUniqueID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author LiuDongFei
 * @date 2022年06月22日 13:22
 */
@Slf4j
@Service
public class DncReportingServiceImpl implements DncReportingService {

    //tokenstr 获取token
    @Autowired
    private RedisUniqueID redisUniqueID;
    //下载程序请求地址
    @Autowired
    private DncRequestPath dncRequestPath;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity<Object> downloadProgram(MomOrder order) {
        /*DNCDto.DownloadProgram downloadProgram = DNCDto.downloadProgram(order);
        log.info("DncReportingServiceImpl [downloadprogram] 调用DNC下载程序接口 请求参数{}", JSON.toJSONString(downloadProgram));
        ResponseEntity<Object> result = restTemplate
                .postForEntity("http://10.0.91.65:9009/downloadprogram", downloadProgram, Object.class);
        log.info("DncReportingServiceImpl [downloadprogram] 调用DNC下载程序接口 响应结果{}", JSON.toJSONString(downloadProgram));
        return result;*/
        return null;
    }
}
