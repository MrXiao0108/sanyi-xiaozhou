package com.dzics.data.appoint.changsha.mom.service.impl.sendhttp;

import com.dzics.data.appoint.changsha.mom.model.vo.ResultVo;
import com.dzics.data.appoint.changsha.mom.service.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Classname HttpWorkReportImpl
 * @Description 生产进度反馈 发送http请求
 * @Date 2022/5/5 13:44
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class HttpWorkReportImpl implements HttpService<ResultVo> {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResultVo sendPost(String innerGroupId, String orderCode, String lineNo, String groupId, String url, String reqJson, Class<ResultVo> responseType) {
        ResponseEntity<ResultVo> forEntity = restTemplate.postForEntity(url, reqJson, responseType);
        ResultVo body = forEntity.getBody();
        if (body!=null){
            body.setStatusCode(forEntity.getStatusCode().value());
        }
        return body;
    }
}
