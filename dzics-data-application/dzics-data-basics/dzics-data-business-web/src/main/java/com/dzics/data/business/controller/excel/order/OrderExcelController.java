package com.dzics.data.business.controller.excel.order;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.business.service.OrderService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.OrderParmsModel;
import com.dzics.data.pub.model.vo.DzOrderDo;
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
public class OrderExcelController {

    @Autowired
    OrderService orderService;

    @ApiOperation(value = "订单管理")
    @GetMapping("/order/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     OrderParmsModel orderParmsModel, HttpServletResponse response) throws IOException {
        String fileNameBase = "订单管理";
        try {
            orderParmsModel.setPage(-1);
            orderParmsModel.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<DzOrderDo>> list = orderService.list(sub, orderParmsModel);
            List<DzOrderDo> data = list.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzOrderDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


}
