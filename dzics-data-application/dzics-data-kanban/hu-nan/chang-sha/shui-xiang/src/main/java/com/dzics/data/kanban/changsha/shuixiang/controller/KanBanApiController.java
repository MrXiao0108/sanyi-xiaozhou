package com.dzics.data.kanban.changsha.shuixiang.controller;

import com.dzics.data.common.base.dto.KbParms;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.service.InterfaceCombination;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 设备生产数量信息
 *
 * @author ZhangChengJun
 * Date 2021/4/26.
 * @since
 */
@Api(tags = {"接口组API"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/methods")
@CrossOrigin
public class KanBanApiController {
    @Autowired
    private InterfaceCombination kbParmsService;
    @Value("${cache.base.name}")
    private String cacheBaseName;

    @ApiOperation(value = "接口组调用")
    @ApiOperationSupport(author = "NeverEnd")
    @PostMapping
    public Result getDeviceproductionQuantity(@Valid @RequestBody KbParms kbParms) {
        kbParms.getOrderNoLineNo().setProjectModule(cacheBaseName);
        Result methods = kbParmsService.getInterFaceMethods(kbParms);
        return methods;
    }

}
