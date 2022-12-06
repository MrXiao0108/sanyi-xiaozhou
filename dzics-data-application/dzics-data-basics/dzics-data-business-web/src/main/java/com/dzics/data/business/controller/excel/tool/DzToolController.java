package com.dzics.data.business.controller.excel.tool;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.business.service.ToolService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.tool.db.model.dao.GetToolInfoDataListDo;
import com.dzics.data.tool.db.model.vo.GetToolInfoDataListVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import com.dzics.data.tool.model.entity.DzToolGroups;
import com.dzics.data.tool.service.DzToolGroupsService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Api(tags = {"Excel管理"},produces = "Excel管理相关接口")
@RestController
@RequestMapping(value = "/api/excel",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class DzToolController {

    @Autowired
    private DzToolGroupsService businessToolGroupsService;
    @Autowired
    private DzicsUserService userService;
    @Autowired
    private ToolService toolService;

    @ApiOperation(value = "刀具管理")
    @GetMapping("/tool/manage/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimit pageLimit, @RequestParam(value = "groupNo", required = false) String groupNo, HttpServletResponse response) throws IOException {
        String fileNameBase = "刀具管理";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            SysUser byUserName = userService.getByUserName(sub);
            String useOrgCode = byUserName.getUseOrgCode();
            Result<List<DzToolGroups>> toolGroupsList = businessToolGroupsService.getToolGroupsList(sub, pageLimit, groupNo, useOrgCode);
            List<DzToolGroups> data = toolGroupsList.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzToolGroups.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "刀具配置")
    @GetMapping("/tool/list/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimit pageLimit, @RequestParam(value = "groupNo", required = false) Integer groupNo, HttpServletResponse response) throws IOException {
        String fileNameBase = "刀具配置";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<DzToolCompensationData>> list = toolService.getToolConfigureList(sub, pageLimit, groupNo);
            List<DzToolCompensationData> data = list.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzToolCompensationData.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "刀具信息数据")
    @GetMapping("/tool/data/list/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimit pageLimit, GetToolInfoDataListVo getToolInfoDataListVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "刀具信息数据";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<GetToolInfoDataListDo>> toolInfoDataList = toolService.getToolInfoDataList(sub, pageLimit, getToolInfoDataListVo);
            List<GetToolInfoDataListDo> data = toolInfoDataList.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), GetToolInfoDataListDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

}
