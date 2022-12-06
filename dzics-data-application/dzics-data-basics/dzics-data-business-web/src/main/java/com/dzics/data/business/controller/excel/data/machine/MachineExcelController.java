package com.dzics.data.business.controller.excel.data.machine;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.business.service.MachineToolService;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pdm.db.model.dao.EquipmentDataDo;
import com.dzics.data.pdm.db.model.dto.EquipmentDownExcelDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentDataVo;
import com.dzics.data.pdm.model.vo.MachineDownExcelVo;
import com.dzics.data.pub.model.vo.EquipmentDo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Api(tags = {"Excel管理"}, produces = "Excel管理相关接口")
@RestController
@RequestMapping(value = "/api/excel",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class MachineExcelController {

    @Autowired
    private MachineToolService dzEquipmentService;
    @Autowired
    private DzEquipmentProNumDao dzEquipmentProNumDao;


    @ApiOperation(value = "机床生产数据")
    @GetMapping("/machine/data/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     SelectEquipmentDataVo dataVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "机床生产数据";
        try {
            dataVo.setPage(-1);
            dataVo.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<EquipmentDataDo>> list = dzEquipmentService.listEquipmentData(sub, EquiTypeEnum.JC.getCode(), dataVo);
            List<EquipmentDataDo> data = list.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), EquipmentDataDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "机床停机数据")
    @GetMapping("/machine/down/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     EquipmentDownExcelDo downExcelDo, HttpServletResponse response) throws IOException {
        String fileNameBase = "机床停机数据";
        try {
            downExcelDo.setPage(-1);
            downExcelDo.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<MachineDownExcelVo>> machineDownExcel = dzEquipmentService.getMachineDownExcel(sub, downExcelDo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), MachineDownExcelVo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(machineDownExcel.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

}
