package com.dzics.data.business.controller.excel.data.equipment;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.FaultRecordParmsReq;
import com.dzics.data.pub.model.vo.FaultRecord;
import com.dzics.data.pub.service.DzRepairHistoryService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
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
public class EquipmentFaultExcelController {

    @Autowired
    private DzRepairHistoryService dzRepairHistoryService;
    @Autowired
    private DzicsUserService userService;

    @ApiOperation(value = "故障记录列表")
    @GetMapping("/fault/list/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimitBase pageLimit, FaultRecordParmsReq parmsReq, HttpServletResponse response) throws IOException {
        String fileNameBase = "故障记录列表";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            SysUser byUserName = userService.getByUserName(sub);
            String useOrgCode = byUserName.getUseOrgCode();
            Result<List<FaultRecord>> list = dzRepairHistoryService.getFaultRecordList(useOrgCode, pageLimit, parmsReq);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), FaultRecord.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(list.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
