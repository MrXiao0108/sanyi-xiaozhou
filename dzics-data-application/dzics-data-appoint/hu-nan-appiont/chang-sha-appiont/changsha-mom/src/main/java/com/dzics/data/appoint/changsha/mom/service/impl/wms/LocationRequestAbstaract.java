package com.dzics.data.appoint.changsha.mom.service.impl.wms;


import com.alibaba.fastjson.JSONObject;
import com.dzics.data.appoint.changsha.mom.config.WmsRequestPath;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzLocation;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.LocationResp;
import com.dzics.data.appoint.changsha.mom.service.wms.WmsServiceAbstract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Service
@Slf4j
public class LocationRequestAbstaract extends WmsServiceAbstract<LocationResp> {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WmsRequestPath wmsRequestPath;

    /**
     * 请求WMS 机械手放货位置申请
     *
     * @param dzLocation RFID信息 订单号 下料点
     * @return
     */
    @Override
    public LocationResp rfidMaterialCodeStation(DzLocation dzLocation) {
        //获取接口地址
        String url = wmsRequestPath.ipPort + wmsRequestPath.LOCATION_REQUEST;
        log.info("请求WMS url:{}  机械手放货位置申请：dzLocation ：{}",url, JSONObject.toJSONString(dzLocation));
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        //设置请求头
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        //请求体
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<DzLocation> formEntity = new HttpEntity<DzLocation>(dzLocation, headers);
        //请求WMS
        ResponseEntity<LocationResp> responseEntity = restTemplate.postForEntity(url, formEntity, LocationResp.class);
        //获取响应体
        LocationResp body = responseEntity.getBody();
        //记录日志
        log.info("请求WMS 机械手放货位置申请 返回结果：body ：{}", JSONObject.toJSONString(body));
        //返回结果
        return body;
    }
}
