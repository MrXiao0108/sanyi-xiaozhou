package com.dzics.data.appoint.changsha.mom.service.wms;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.appoint.changsha.mom.config.WmsRequestPath;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzCallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzLocation;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzOrderCompleted;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.CallFrameResp;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.WmsRespone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * wms 接口
 *
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Component
@Slf4j
public abstract class WmsServiceAbstract<T> implements WmsService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WmsRequestPath wmsRequestPath;

    /**
     * 呼叫料框
     *
     * @param dzCallFrame RFID信息 订单号 物料号
     * @return
     */
    @Override
    public CallFrameResp callFrame(DzCallFrame dzCallFrame) {
        //获取呼叫料框的接口地址
        String url = wmsRequestPath.ipPort + wmsRequestPath.CALL_FRAME;
        //记录日志 请求WMS url:{}, 呼叫料框：dzCallFrame ：{}
        log.info("请求WMS  url:{} 呼叫料框：dzCallFrame ：{}", url, JSONObject.toJSONString(dzCallFrame));
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        //设置浏览器解析格式为json
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        //设置请求头
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        //创建请求体
        HttpEntity<DzCallFrame> formEntity = new HttpEntity<DzCallFrame>(dzCallFrame, headers);
        //发送请求
        ResponseEntity<CallFrameResp> responseEntity = restTemplate.postForEntity(url, formEntity, CallFrameResp.class);
        //获取响应体
        CallFrameResp body = responseEntity.getBody();
        //记录日志 请求WMS url:{}, 呼叫料框：dzCallFrame ：{}
        log.info("请求WMS 呼叫料框 返回结果：body ：{}", JSONObject.toJSONString(body));
        //返回结果
        return body;
    }

    /**
     * 请求WMS 服务器
     *  机械手放货位置申请
     * @param dzLocation RFID信息 订单号 下料点
     * @return
     */
    @Override
    public abstract T rfidMaterialCodeStation(DzLocation dzLocation);


    /**
     * 订单完成信号
     *
     * @param orderNum 订单号
     * @return
     */
    @Override
    public WmsRespone orderCompleted(DzOrderCompleted orderNum) {
        String url = wmsRequestPath.ipPort + wmsRequestPath.ORDER_COMPLETED;
        log.info("请求WMS url:{}, 订单完成信号：dzCallFrame ：{}", url, JSONObject.toJSONString(orderNum));
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<DzOrderCompleted> formEntity = new HttpEntity<DzOrderCompleted>(orderNum, headers);
        ResponseEntity<WmsRespone> responseEntity = restTemplate.postForEntity(url, formEntity, WmsRespone.class);
        WmsRespone body = responseEntity.getBody();
        log.info("请求WMS 订单完成信号 返回结果：body ：{}", JSONObject.toJSONString(body));
        return body;
    }

}
