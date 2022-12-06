package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.MomPackingPointMaterialsDao;
import com.dzics.data.appoint.changsha.mom.model.constant.TcpType;
import com.dzics.data.appoint.changsha.mom.model.dao.GetPointMaterialDo;
import com.dzics.data.appoint.changsha.mom.model.dto.MaterialFrameRes;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialPoint;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomPackingPointMaterials;
import com.dzics.data.appoint.changsha.mom.model.vo.MomQrCodeVo;
import com.dzics.data.appoint.changsha.mom.model.vo.PointMaterialsModel;
import com.dzics.data.appoint.changsha.mom.service.*;
import com.dzics.data.appoint.changsha.mom.util.ListUtil;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.RobRequestException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xnb
 * @since 2022-10-31
 */
@Service
@Slf4j
public class MomPackingPointMaterialsServiceImpl extends ServiceImpl<MomPackingPointMaterialsDao, MomPackingPointMaterials> implements MomPackingPointMaterialsService {
    @Value("${order.code}")
    private String orderCode;

    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MomMaterialPointService materialPointService;
    @Autowired
    private MOMAGVService momagvService;
    @Autowired
    private MOMBaseService momBaseService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private ListUtil listUtil;


    @Override
    public List<GetPointMaterialDo> getMaterialsByPoint(PointMaterialsModel pointMaterialsModel) {

        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String runModel = cachingApi.getMomRunModel();
        String key = orderCode+ TcpType.Packing_AGV+ pointMaterialsModel.getExternalCode();
        List<GetPointMaterialDo>list = new ArrayList<>();

        if(redisUtil.hasKey(key)){
            List<MomQrCodeVo>qrCodes = redisUtil.lGet(key, 0, -1);
            List<String> dataList = qrCodes.stream().map(p -> p.getWorkOrderNo()).collect(Collectors.toList());
            List<String> orders = dataList.stream().distinct().collect(Collectors.toList());
            MomMaterialPoint pointServiceOne = materialPointService.getOne(new QueryWrapper<MomMaterialPoint>().eq("external_code", pointMaterialsModel.getExternalCode()));
            for (String order : orders) {
                GetPointMaterialDo pointMaterial = new GetPointMaterialDo();
                pointMaterial.setMomOrder(order);
                Integer num = listUtil.getFieldNum(dataList, order);
                pointMaterial.setQuantity(String.valueOf(num));
                if("auto".equals(runModel)){
                    pointMaterial.setFrameCode("自动物流");
                }else{
                    pointMaterial.setFrameCode(pointServiceOne.getFrameCode());
                }
                MomOrder momOrder = momOrderService.getOne(new QueryWrapper<MomOrder>().eq("order_id", line.getOrderId()).eq("WipOrderNo",order));
                if(momOrder==null){
                    pointMaterial.setProductNo("未知");
                }else{
                    pointMaterial.setProductNo(momOrder.getProductNo());
                }
                pointMaterial.setExternalCode(pointMaterialsModel.getExternalCode());
                list.add(pointMaterial);
            }
        }
        return list;
    }

    @Override
    public void handlePackingData(String point) {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        try {
            String runModel = cachingApi.getMomRunModel();
            if(StringUtils.isEmpty(runModel)){
                throw new CustomException(CustomExceptionType.Parameter_Exception, CustomResponseCode.ERR95.getChinese());
            }
            String ContainerListNo = "";
            MomMaterialPoint materialPoint = materialPointService.getOne(new QueryWrapper<MomMaterialPoint>()
                    .eq("line_id", line.getId())
                    .eq("external_code", point));
            if(materialPoint==null){
                log.error("MomPackingPointMaterialsServiceImpl [handlePackingData] 查询结果：materialPoint为空，line_id：{}，external_code：{}",line.getId(),point);
                throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR17.getChinese());
            }
            if("auto".equals(runModel)){
                MaterialFrameRes stringPalletType = momBaseService.getStringPalletType("", "", orderCode, line.getLineNo(), point, "");
                if (StringUtils.isEmpty(stringPalletType.getPalletNo())) {
                    log.error("MomPackingPointMaterialsServiceImpl [handlePackingData] 装框完成反馈：中控-->Mom查询料框信息失败，Mom返回料框编号为空");
                    throw new RobRequestException(CustomResponseCode.ERR62.getChinese());
                }
                ContainerListNo = stringPalletType.getPalletNo();
            }else{
                ContainerListNo = materialPoint.getFrameCode();
            }
            List<MomQrCodeVo>list = redisUtil.lGet(line.getOrderNo() + TcpType.Packing_AGV + point + "copy", 0, -1);
            momagvService.packingComplete(list,ContainerListNo,materialPoint.getPointModel(),point);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }

    @Override
    public List<GetPointMaterialDo> putMaterialsByPoint(PointMaterialsModel pointMaterialsModel) {
        if(StringUtils.isEmpty(pointMaterialsModel.getMomOrder()) || StringUtils.isEmpty(pointMaterialsModel.getQrCode())){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR,CustomResponseCode.ERR12.getChinese());
        }
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        List<MomMaterialPoint> pointList = materialPointService.list(new QueryWrapper<MomMaterialPoint>().eq("line_id", line.getId()).eq("point_model", "TL"));
        //未配置下料点信息或者下料点信息数量配置不等于2，校验不通过
        if(CollectionUtils.isEmpty(pointList) || pointList.size()!=2){
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR,CustomResponseCode.ERR961.getChinese());
        }
        //重定向新料点，根据料点可直接获取到料框
        if(pointMaterialsModel.getExternalCode().equals(pointList.get(0).getExternalCode())){
            pointMaterialsModel.setNewExCode(pointList.get(1).getExternalCode());
        }else{
            pointMaterialsModel.setNewExCode(pointList.get(0).getExternalCode());
        }
        //获取旧料框的物料实时详情信息
        List<MomQrCodeVo> oldMaterials = new ArrayList<>();
        if(redisUtil.hasKey(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getExternalCode())){
            oldMaterials = redisUtil.lGet(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getExternalCode(), 0, -1);
        }
        //获取新料框的物料实时详情信息
        List<MomQrCodeVo> newMaterials = new ArrayList<>();
        if(redisUtil.hasKey(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getNewExCode())){
            newMaterials = redisUtil.lGet(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getNewExCode(), 0, -1);
        }

        for(int i=0;i<oldMaterials.size();i++){
            if(pointMaterialsModel.getMomOrder().equals(oldMaterials.get(i).getWorkOrderNo()) && pointMaterialsModel.getQrCode().equals(oldMaterials.get(i).getQrCode())){
                newMaterials.add(oldMaterials.get(i));
                oldMaterials.remove(i);
            }
        }

        redisUtil.del(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getExternalCode());
        redisUtil.del(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getNewExCode());
        redisUtil.lSet(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getExternalCode(),oldMaterials);
        redisUtil.lSet(line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getNewExCode(),newMaterials);
        return getMaterialsByPoint(pointMaterialsModel);
    }

    @Override
    public List<GetPointMaterialDo> getMaterialsByPointMomOrder(PointMaterialsModel pointMaterialsModel) {
        if(StringUtils.isEmpty(pointMaterialsModel.getMomOrder())){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR,CustomResponseCode.ERR12.getChinese());
        }
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String key = line.getOrderNo() + TcpType.Packing_AGV + pointMaterialsModel.getExternalCode();
        List<MomQrCodeVo>list = redisUtil.lGet(key, 0, -1);
        List<GetPointMaterialDo>materialDoList=new ArrayList<>();
        for (MomQrCodeVo momQrCodeVo : list) {
            if(pointMaterialsModel.getMomOrder().equals(momQrCodeVo.getWorkOrderNo())){
                GetPointMaterialDo getPointMaterialDo = new GetPointMaterialDo();
                getPointMaterialDo.setExternalCode(pointMaterialsModel.getExternalCode());
                getPointMaterialDo.setQrCode(momQrCodeVo.getQrCode());
                getPointMaterialDo.setMomOrder(momQrCodeVo.getWorkOrderNo());
                MomOrder momOrder = momOrderService.getOne(new QueryWrapper<MomOrder>().eq("order_id", line.getOrderId()).eq("WipOrderNo",momQrCodeVo.getWorkOrderNo()));
                if(momOrder==null){
                    getPointMaterialDo.setProductNo("未知");
                }else{
                    getPointMaterialDo.setProductNo(momOrder.getProductNo());
                }
                materialDoList.add(getPointMaterialDo);
            }
        }
        return materialDoList;
    }
}
