package com.dzics.data.appoint.changsha.mom.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.appoint.changsha.mom.model.dao.GetPointMaterialDo;
import com.dzics.data.appoint.changsha.mom.model.dao.MomMaterialPointDo;
import com.dzics.data.appoint.changsha.mom.model.dao.SearchAGVParmsDo;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchAGVParms;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialPoint;
import com.dzics.data.appoint.changsha.mom.model.vo.*;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.MomMaterialPointService;
import com.dzics.data.appoint.changsha.mom.service.MomPackingPointMaterialsService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.DzicsStationCode;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author LiuDongFei
 * @date 2022年07月08日 20:21
 */
@Api(tags = {"AGV投料管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
@RequestMapping("api/mom/materialPoint")
public class MomMaterialPointController {

    @Autowired
    private MomMaterialPointService materialPointService;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomPackingPointMaterialsService pointMaterialsService;

    @ApiOperation(value = "查询AGV投料信息", notes = "查询AGV投料信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 1)
    @GetMapping("/search")
    public Result<MomMaterialPointDo> list(SearchAGVParms parms) {
        return materialPointService.listQuery(parms);
    }

    @ApiOperation(value = "新增AGV投料信息", notes = "新增AGV投料信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 2)
    @PostMapping("/add")
    public Result adddata(@Valid @RequestBody AddMaterialVo addMaterialVo) {
        return materialPointService.addData(addMaterialVo);
    }

    @ApiOperation(value = "修改AGV投料信息", notes = "修改AGV投料信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 4)
    @PutMapping("/update")
    public Result alterdata(@Valid @RequestBody UpdateMaterialVo updateMaterialVo) {
        return materialPointService.alterData(updateMaterialVo);
    }

    @ApiOperation(value = "删除AGV投料信息", notes = "删除AGV投料信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 3)
    @DeleteMapping("/{materialPointId}")
    public Result deldata(@PathVariable("materialPointId") String materialPointId) {
        return materialPointService.delData(materialPointId);
    }

    @ApiOperation(value = "查询Dz工位编号",consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/stationCode")
    public Result<DzicsStationCode> getDzStationCode(@RequestParam(value = "lineId",required = true) @ApiParam(value = "产线Id",required = true) String lineId) {
        return materialPointService.getDzStationCode(lineId);
    }

    @ApiOperation(value = "AGV投料导出", notes = "AGV投料导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportdata(HttpServletResponse response, SearchAGVParms parms) throws IOException {
        String fileNameBase = "AGV投料信息";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename = " + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            parms.setPage(-1);
            Result<List<SearchAGVParmsDo>> result = materialPointService.listQuery(parms);
            EasyExcel.write(response.getOutputStream(), SearchAGVParmsDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(result.getData());
        } catch (Exception e) {
            log.error("导出:{}失败:{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("UTF-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @ApiOperation(value = "查询AGV呼叫模式", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/get/mom/run/model")
    public Result<AgvModel> agvRunModel() {
        String runModel = cachingApi.getMomRunModel();
        if (StringUtils.isEmpty(runModel)) {
            throw new CustomException(CustomExceptionType.Parameter_Exception, CustomResponseCode.ERR95.getChinese());
        }
        int rm = runModel.equals("auto") ? 1 : 0;
        AgvModel agvModel = new AgvModel();
        agvModel.setRm(rm);
        return Result.OK(agvModel);
    }

    @ApiOperation(value = "修改AGV呼叫模式", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/put/mom/run/model")
    public Result updateAgvRunModel(@Valid @RequestBody AgvModel logId){
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String momRunModel = cachingApi.updateAgvRunModel(logId.getRm(),line);
        return Result.OK(momRunModel);
    }

    @ApiOperation(value = "料框编号绑定", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/put/mom/farmCode")
    public Result updateForameCode(@Valid @RequestBody FarmCodeModel farmCodeModel){
        MomMaterialPoint materialPoint = materialPointService.getById(farmCodeModel.getMaterialPointId());
        materialPoint.setFrameCode(farmCodeModel.getFarmCode());
        boolean b = materialPointService.updateById(materialPoint);
        return Result.ok(b);
    }

    @ApiOperation(value = "查询料框物料详情",consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/get/mom/farm/materials")
    public Result<List<GetPointMaterialDo>> getPointMaterials(@Valid PointMaterialsModel pointMaterialsModel){
        return Result.ok(pointMaterialsService.getMaterialsByPoint(pointMaterialsModel));
    }

    @ApiOperation(value = "物料移框",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/put/mom/farm/materials/remove")
    public Result<List<GetPointMaterialDo>> PutPointMaterialsRemove(@RequestBody @Valid PointMaterialsModel materialVo){
        return Result.ok(pointMaterialsService.putMaterialsByPoint(materialVo));
    }

    @ApiOperation(value = "编辑料框物料详情",consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/put/mom/farm/materials")
    public Result<List<GetPointMaterialDo>> PutPointMaterials(@Valid PointMaterialsModel pointMaterialsModel){
        return Result.ok(pointMaterialsService.getMaterialsByPointMomOrder(pointMaterialsModel));
    }
}
