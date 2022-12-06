package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.config.MapConfig;
import com.dzics.data.appoint.changsha.mom.db.dao.MomMaterialPointDao;
import com.dzics.data.appoint.changsha.mom.model.dao.MomMaterialPointDo;
import com.dzics.data.appoint.changsha.mom.model.dto.MomUpPoint;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchAGVParms;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialPoint;
import com.dzics.data.appoint.changsha.mom.model.vo.AddMaterialVo;
import com.dzics.data.appoint.changsha.mom.model.vo.UpdateMaterialVo;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.MomMaterialPointService;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzWorkStationManagementDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.DzicsStationCode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 料点编码 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-02
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class MomMaterialPointServiceImpl extends ServiceImpl<MomMaterialPointDao, MomMaterialPoint> implements MomMaterialPointService {

    @Autowired
    private MomMaterialPointDao momMaterialPointDao;

    @Autowired
    private DzWorkStationManagementDao stationManagementMapper;
    @Autowired
    private DzProductionLineDao productionLineMapper;
    @Autowired
    private MomMaterialPointService momMaterialPointService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    private CachingApi cachingApi;

    @Override
    public MomUpPoint getStationCode(String basketType, String orderCode, String lineNo) {
        return momMaterialPointDao.getStationCode(basketType, orderCode, lineNo);
    }

    @Override
    public String getNextPoint(String orderCode, String lineNo, String basketType) {
        QueryWrapper<MomMaterialPoint> wp1 = new QueryWrapper<>();
        wp1.eq("order_no", orderCode);
        wp1.eq("line_no", lineNo);
        wp1.eq("in_island_code", basketType);
        List<MomMaterialPoint> list = list(wp1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0).getStationId();
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR81);
    }

    @Override
    public String getNextPoint(String orderId, String id) {
        QueryWrapper<MomMaterialPoint> wp1 = new QueryWrapper<>();
        wp1.eq("order_id", orderId);
        wp1.eq("line_id", id);
        wp1.eq("next_point", true);
        List<MomMaterialPoint> list = list(wp1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0).getStationId();
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR81);
    }

    @Override
    public String getNextPoint() {
        QueryWrapper<MomMaterialPoint> wp1 = new QueryWrapper<>();
        wp1.eq("next_point", 1);
        List<MomMaterialPoint> list = list(wp1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0).getStationId();
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR81);
    }

    /**
     * 查询AGV投料信息
     *
     * @param parms
     * @return
     */
    @Override
    public Result listQuery(SearchAGVParms parms) {
        if (parms.getPage() != -1) {
            PageHelper.startPage(parms.getPage(), parms.getLimit());
        }
        LambdaQueryWrapper<MomMaterialPoint> query = Wrappers.<MomMaterialPoint>lambdaQuery();
        if (StringUtils.hasText(parms.getLineId())) {
            query.eq(MomMaterialPoint::getLineId, parms.getLineId());
        }
        if (StringUtils.hasText(parms.getExternalCode())) {
            query.eq(MomMaterialPoint::getExternalCode, parms.getExternalCode());
        }
        List<MomMaterialPoint> list = this.list(query);
        PageInfo<MomMaterialPoint> info = new PageInfo<>(list);

        DzProductionLine productionLine = cachingApi.getOrderIdAndLineId();
        List<MomMaterialPointDo> resultList = new ArrayList<>();
        list.forEach(m -> {
            MomMaterialPointDo momMaterialPointDo = new MomMaterialPointDo();
            momMaterialPointDo.setMaterialPointId(m.getMaterialPointId());
            momMaterialPointDo.setLineId(String.valueOf(m.getLineId()));
            momMaterialPointDo.setLineName(productionLine.getLineName());
            momMaterialPointDo.setExternalCode(m.getExternalCode());
            momMaterialPointDo.setExternalRegion(m.getExternalRegion());
            momMaterialPointDo.setLineNode(m.getLineNode());
            momMaterialPointDo.setInIslandCode(m.getInIslandCode());
            momMaterialPointDo.setStationId(m.getStationId());
            momMaterialPointDo.setPointModel(m.getPointModel());
            momMaterialPointDo.setNextPoint(m.getNextPoint());
            momMaterialPointDo.setPalletType(m.getPalletType());
            momMaterialPointDo.setFrameCode(m.getFrameCode());
            resultList.add(momMaterialPointDo);
        });
        return Result.ok(resultList, info.getTotal());
    }

    /**
     * 新增投料信息
     *
     * @param addMaterialVo
     * @return
     */
    @Override
    public Result addData(AddMaterialVo addMaterialVo) {
        MomMaterialPoint momMaterialPoint = new MomMaterialPoint();
        DzProductionLine dzProductionLine = productionLineMapper.selectOne(new QueryWrapper<DzProductionLine>().eq("id", addMaterialVo.getLineId()));
        if (dzProductionLine == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR592);
        }
        momMaterialPoint.setOrderId(Long.parseLong(dzProductionLine.getOrderId()));
        momMaterialPoint.setLineId(Long.parseLong(dzProductionLine.getId()));
        momMaterialPoint.setLineNo(dzProductionLine.getLineNo());
        momMaterialPoint.setOrderNo(dzProductionLine.getOrderNo());
        momMaterialPoint.setExternalCode(addMaterialVo.getExternalCode().trim());
        momMaterialPoint.setExternalRegion(addMaterialVo.getExternalRegion());
        momMaterialPoint.setPointModel(addMaterialVo.getPointModel());
        momMaterialPoint.setLineNode(addMaterialVo.getLineNode());
        momMaterialPoint.setInIslandCode(addMaterialVo.getInIslandCode());
        momMaterialPoint.setStationId(addMaterialVo.getStationId());
        momMaterialPoint.setNextPoint(addMaterialVo.getNextPoint());
        momMaterialPoint.setPalletType(addMaterialVo.getPalletType());
        momMaterialPoint.setDelFlag(true);
        momMaterialPointService.save(momMaterialPoint);
        return Result.ok();
    }

    public Result cleanCache(MomMaterialPoint momMaterialPoint) {
        String url = "";
        String orderNo = momMaterialPoint.getOrderNo();
        String lineNo = momMaterialPoint.getLineNo();
        try {
            Map<String, String> maps = mapConfig.getMaps();
            String plcIp = maps.get(orderNo + lineNo);
            if (CollectionUtils.isNotEmpty(maps) && !StringUtils.isEmpty(plcIp)) {
                url = "http://" + plcIp + ":8107/api/receive/data/get/position";
                ResponseEntity<Result> resultResponseEntity = restTemplate.postForEntity(url, momMaterialPoint, Result.class);
                Result body = resultResponseEntity.getBody();
                log.info("清除工位配置模块缓存 到单岛 订单：{},产线：{},响应参数：{},请求参数：{}", orderNo, lineNo, body, JSONObject.toJSONString(body), JSONObject.toJSONString(momMaterialPoint));
                return body;
            } else {
                log.error("清除工位配置模块缓存 IP 配置不存在orderNo：{},lineNo：{},maps:{}", orderNo, lineNo, maps);
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
        } catch (Throwable throwable) {
            log.error("清除工位配置模块缓存 订单：{},产线：{},错误信息：{},请求参数：{}", orderNo, lineNo, throwable.getMessage(), JSONObject.toJSONString(momMaterialPoint), throwable);
            return Result.ok(throwable.getMessage());
        }
    }

    @Override
    public Result alterData(UpdateMaterialVo updateMaterialVo) {
        //判断产线Id是否一致
        DzProductionLine dzProductionLine = productionLineMapper.selectOne(new QueryWrapper<DzProductionLine>().eq("id", updateMaterialVo.getLineId()));
        if (dzProductionLine == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR59);
        }
        String lineNo = dzProductionLine.getLineNo();
        String orderNo = dzProductionLine.getOrderNo();
        Long orderid = Long.parseLong(dzProductionLine.getLineNo());
        Long lineid = Long.parseLong(dzProductionLine.getId());
        if (StringUtils.isEmpty(lineNo) || StringUtils.isEmpty(orderNo)) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR58);
        }
        MomMaterialPoint newmom = momMaterialPointDao.selectById(updateMaterialVo.getMaterialPointId());
        if (newmom == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR17);
        }
        newmom.setOrderId(orderid);
        newmom.setLineId(lineid);
        newmom.setLineNo(lineNo);
        newmom.setPointModel(updateMaterialVo.getPointModel());
        newmom.setOrderNo(orderNo);
        newmom.setExternalCode(updateMaterialVo.getExternalCode());
        newmom.setExternalRegion(updateMaterialVo.getExternalRegion());
        newmom.setLineNode(updateMaterialVo.getLineNode());
        newmom.setInIslandCode(updateMaterialVo.getInIslandCode());
        newmom.setStationId(updateMaterialVo.getStationId());
        newmom.setUpdateTime(new Date());
        newmom.setNextPoint(updateMaterialVo.getNextPoint());
        newmom.setPalletType(updateMaterialVo.getPalletType());
        momMaterialPointDao.updateById(newmom);
        return Result.ok();
    }

    @Override
    public Result delData(String materialPointId) {
        QueryWrapper<MomMaterialPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_point_id", materialPointId);
        momMaterialPointDao.delete(queryWrapper);
        return Result.ok();
    }

    @Override
    public Result getDzStationCode(String lineId) {
        List<DzicsStationCode> stationCodes = stationManagementMapper.getDzicsStationCode(lineId);
        return Result.ok(stationCodes);
    }

}
