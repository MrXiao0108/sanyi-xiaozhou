package com.dzics.data.business.controller.machinetool;


import com.dzics.data.business.service.MachineToolService;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.dao.EquipmentDataDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentDataVo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"机床历史数据"}, produces = "机床历史数据管理关接口")
@RequestMapping("/toolData")
@RestController
public class ToolDataController {
    @Autowired
    private MachineToolService dzEquipmentService;

//    @OperLog(operModul = "分页查询机床数据列表", operType = OperType.QUERY, operDesc = "分页查询机床数据列表", operatorType = "后台")
    @ApiOperation(value = "分页查询机床数据列表 ")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<EquipmentDataDo> listEquipmentData(SelectEquipmentDataVo dataVo,
                                                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ){
        dataVo.setEquipmentType(EquiTypeEnum.JC.getCode());
        Result listResult = dzEquipmentService.listEquipmentData(sub, EquiTypeEnum.JC.getCode(), dataVo);
        return listResult;
    }
}
