package com.dzics.data.business.controller.excel.detection;

import com.dzics.data.business.service.ProductCheckRecordService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@Slf4j
@Api(tags = {"Excel管理"}, produces = "Excel管理相关接口")
@RestController
@RequestMapping("/api/excel")
public class DetectionExcelController {
     @Autowired
     ProductCheckRecordService busDetectorDataService;
    

//    @OperLog(operModul = "产品检测配置", operType = OperType.QUERY, operDesc = "检测管理", operatorType = "后台")
//    @ApiOperation(value = "检测管理", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @GetMapping("/robot/data/download")
//    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
//                     ProDuctCheck proDuctCheck, HttpServletResponse response) throws IOException {
//        String fileNameBase = "产品检测配置";
//        try {
//            proDuctCheck.setPage(-1);
//            proDuctCheck.setLimit(FinalCode.SELECT_SUM_EXCEL);
//            Result<List<LinkedHashMap<String, Object>>> list = busDetectorDataService.queryProDetectorItem(proDuctCheck, sub);
//            List<LinkedHashMap<String, Object>> data = list.getData();
//            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
//            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
//            EasyExcel.write(response.getOutputStream(), EquipmentDataDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
//        }catch (Exception e){
//            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
//            response.setCharacterEncoding("utf-8");
//            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
//            response.getWriter().println(JSON.toJSONString(error));
//        }
//    }

//        @OperLog(operModul = "检测记录", operType = OperType.QUERY, operDesc = "检测管理", operatorType = "后台")
//        @ApiOperation(value = "检测管理", consumes = MediaType.APPLICATION_JSON_VALUE)
//        @GetMapping("/robot/data/download")
//        public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
//                         @Valid DetectorDataQuery detectorDataQuery, HttpServletResponse response) throws IOException {
//            String fileNameBase = "检测记录";
//            try {
//                detectorDataQuery.setPage(-1);
//                detectorDataQuery.setLimit(FinalCode.SELECT_SUM_EXCEL);
//                Result result = busDetectorDataService.selDetectorData(detectorDataQuery, sub);
//                String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
//                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
//                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
//                EasyExcel.write(response.getOutputStream(), EquipmentDataDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite((List) result);
//            }catch (Exception e){
//                log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
//                response.setCharacterEncoding("utf-8");
//                Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
//                response.getWriter().println(JSON.toJSONString(error));
//            }
//        }


//    @OperLog(operModul = "检测值走势图", operType = OperType.QUERY, operDesc = "检测管理", operatorType = "后台")
//    @ApiOperation(value = "检测管理", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @GetMapping("/robot/data/download")
//    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
//                     @Valid DetectorDataQuery detectorDataQuery, HttpServletResponse response) throws IOException {
//        String fileNameBase = "检测值走势图";
//        try {
//            detectorDataQuery.setPage(-1);
//            detectorDataQuery.setLimit(FinalCode.SELECT_SUM_EXCEL);
//            Result result = busDetectorDataService.selDetectorData(detectorDataQuery, sub);
//            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
//            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
//            EasyExcel.write(response.getOutputStream(), EquipmentDataDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite((List) result);
//        }catch (Exception e){
//            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
//            response.setCharacterEncoding("utf-8");
//            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
//            response.getWriter().println(JSON.toJSONString(error));
//        }
//    }



}
