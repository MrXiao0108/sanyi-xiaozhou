package com.dzics.data.appoint.changsha.mom.controller;


import com.dzics.data.appoint.changsha.mom.service.MomPackingPointMaterialsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xnb
 * @since 2022-10-31
 */
@Api(tags = {"Mom料框绑定物料信息相关接口"}, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@RequestMapping("api/mom/momPackingPointMaterials")
public class MomPackingPointMaterialsController {

    @Autowired
    private MomPackingPointMaterialsService pointMaterialsService;


    @ApiOperation(value = "发送mom装框信息接口", notes = "发送mom装框信息接口", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/send/mom/packing")
    public void SendMomPointMaterial(String point){
        pointMaterialsService.handlePackingData(point);
    }

}

