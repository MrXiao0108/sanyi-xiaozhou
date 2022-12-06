package com.dzics.data.appoint.changsha.mom.service.impl.wms;


import com.alibaba.fastjson.JSONObject;
import com.dzics.data.appoint.changsha.mom.config.WmsRequestPath;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzLocation;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.WmsRespone;
import com.dzics.data.appoint.changsha.mom.service.wms.WmsServiceAbstract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Component
@Slf4j
public  class PutCompletedAbstaract extends WmsServiceAbstract<WmsRespone> {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WmsRequestPath wmsRequestPath;
    /**
     * 请求WMS 放料完成
     *
     * @param dzLocation         RFID信息 订单号 下料点
     * @return
     */
    @Override
    public WmsRespone rfidMaterialCodeStation(DzLocation dzLocation) {
        String url = wmsRequestPath.ipPort+wmsRequestPath.PUT_COMPLETED;
        log.info("请求WMS url:{} 放料完成：dzCallFrame ：{}",url, JSONObject.toJSONString(dzLocation));
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<DzLocation> formEntity = new HttpEntity<DzLocation>(dzLocation, headers);
        ResponseEntity<WmsRespone> responseEntity = restTemplate.postForEntity(url, formEntity, WmsRespone.class);
        WmsRespone body = responseEntity.getBody();
        log.info("请求WMS 放料完成 返回结果：body ：{}",JSONObject.toJSONString(body));
        return body;
    }
}
