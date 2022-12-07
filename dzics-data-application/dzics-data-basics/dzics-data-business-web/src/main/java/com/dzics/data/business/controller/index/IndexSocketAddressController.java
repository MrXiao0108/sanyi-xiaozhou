package com.dzics.data.business.controller.index;

import com.dzics.data.business.model.vo.ActiveTip.ActiveTipsVo;
import com.dzics.data.business.service.ActiveTipsService;
import com.dzics.data.business.service.EquipmentProNumService;
import com.dzics.data.business.service.EquipmentService;
import com.dzics.data.business.service.LogService;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.dao.QualifiedAndOutputDo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.pdm.model.vo.GetDayAndMonthDataDo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页socket接口地址获取
 *
 * @author ZhangChengJun
 * Date 2021/3/4.
 * @since
 */
@Api(tags = {"首页接口"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/index")
@Controller
public class IndexSocketAddressController {
   @Autowired
   private EquipmentProNumService proNumService;
   @Autowired
   private EquipmentService equipmentService;
   @Autowired
   private LogService logService;
   @Autowired
   private ActiveTipsService activeTipsService;

   @ApiOperation(value = "首页查询产出率和合格率")
   @ApiOperationSupport(author = "jq", order = 1)
   @GetMapping("/getOutputAndQualified/{lineId}")
   public Result<QualifiedAndOutputDo> getOutputAndQualified(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                             @PathVariable("lineId")String lineId) {
      Result result = proNumService.getOutputAndQualified(lineId);
      return result;
   }

   @ApiOperation(value = "首页查询产线日产和月产")
   @ApiOperationSupport(author = "jq", order = 1)
   @GetMapping("/geDayAndMonthData/{lineId}")
   public Result<GetDayAndMonthDataDo> geDayAndMonthData(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                         @PathVariable("lineId")String lineId) {
      Result result = proNumService.geDayAndMonthDataV2(lineId);
      return result;
   }

   @ApiOperation(value = "首页查询设备信息")
   @ApiOperationSupport(author = "jq", order = 1)
   @GetMapping("/geEquipmentState/{lineId}")
   public Result geEquipmentState(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                         @PathVariable("lineId")String lineId) {
      Result result = equipmentService.geEquipmentState(lineId);
      return result;
   }

   @ApiOperation(value = "首页查询设备告警日志")
   @GetMapping("/getIndexWarnLog/{orderId}")
   public Result<List<SysRealTimeLogs>> getIndexWarnLog(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                        @PathVariable("orderId")String orderId) {
      return Result.ok(logService.getIndexWarnLog(orderId));
   }

   @ApiOperation(value = "业务端到期消息推送")
   @GetMapping("/getActiveTipsVo")
   public Result<List<ActiveTipsVo>> getActiveTipsVo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub) {
      return new Result(CustomExceptionType.OK, activeTipsService.getActiveTipsVo(), activeTipsService.getActiveTipsVo().size());
   }
}
