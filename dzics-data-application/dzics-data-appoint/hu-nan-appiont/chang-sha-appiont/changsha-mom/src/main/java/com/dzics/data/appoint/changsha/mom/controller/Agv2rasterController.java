package com.dzics.data.appoint.changsha.mom.controller;

import cn.hutool.json.JSONUtil;
import com.dzics.data.appoint.changsha.mom.service.MOMAGVService;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * 18号厂房 AGV握手
 *
 * @author: van
 * @since: 2022-08-14
 */
@Api(tags = {"AGV握手-18号厂房"})
@Slf4j
@RestController
@RequestMapping("/agv2raster/agv2raster")
public class Agv2rasterController {

    @Autowired
    private MOMAGVService momagvService;

    @ApiOperation(value = "agv握手")
    @PostMapping
    public Map<String, Object> agv2raster(@RequestBody Map<String, Agv2rasterModel> map) {
        log.info(" Agv2rasterController [agv2raster] map{}", JSONUtil.toJsonStr(map));
        Agv2rasterModel model = map.get("call_request_raster");
        HashMap<String, Object> result = new HashMap<>();
        if (ObjectUtils.isEmpty(model)) {
            Map<String, Object> fail = new HashMap<>();
            fail.put("return_code", 400);
            fail.put("return_desc", "");
            result.put("request_result", fail);
            return result;
        }
        Map<String, Object> handleResult = momagvService.handshakeHandle(model.getStatus());
        result.put("request_result", handleResult);
        log.info(" Agv2rasterController [agv2raster] result{}", JSONUtil.toJsonStr(result));
        return result;
    }

    @Data
    static class Agv2rasterModel {

        private String order_id;

        private String agv_id;

        private String loc_id;

        private String pallet_no;

        private String status;

        private String custom_param1;

        private String custom_param2;
    }

    public enum Status {

        IN_REQ("1"),
        IN_COMPLETE("2"),
        OUT_REQ("3"),
        OUT_COMPLETE("4");

        private final String val;

        Status(String val) {
            this.val = val;
        }

        public String val() {
            return val;
        }
    }
}
