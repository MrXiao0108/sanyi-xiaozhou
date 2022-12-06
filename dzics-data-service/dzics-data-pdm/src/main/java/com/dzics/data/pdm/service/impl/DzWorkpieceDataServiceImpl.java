package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzWorkpieceDataDao;
import com.dzics.data.pdm.model.dto.CheckItems;
import com.dzics.data.pdm.model.entity.DzWorkpieceData;
import com.dzics.data.pdm.service.DzWorkpieceDataService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备检测数据V2新版记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Service
@Slf4j
public class DzWorkpieceDataServiceImpl extends ServiceImpl<DzWorkpieceDataDao, DzWorkpieceData> implements DzWorkpieceDataService {
    @Autowired
    DzWorkpieceDataDao dzWorkpieceDataMapper;

    @Override
    public Result processingData(RabbitmqMessage rabbitmqMessage) {

        String[] split = rabbitmqMessage.getMessage().split("\\|");
        String tcp = split[0];
        String msg = split[1];
        if ("A816".equals(tcp)) {
            String replace = msg.replace("[", "").replace("]", "");
            String[] data = replace.split(",");
            String authCode = data[0];//交换码
            String qrCode = data[1];//二维码
            PageHelper.startPage(1, 1);
            QueryWrapper<DzWorkpieceData> wrapper = new QueryWrapper<DzWorkpieceData>()
                    .eq("auth_code", authCode)
                    .orderByDesc("detector_time");
            List<DzWorkpieceData> dataList = dzWorkpieceDataMapper.selectList(wrapper);
            PageInfo<DzWorkpieceData> list = new PageInfo<>(dataList);
            if (list.getList().size() != 1) {
                log.warn("交换码绑定的数据不存在:{}", authCode);
                return null;
            }
            DzWorkpieceData dzWorkpieceData = list.getList().get(0);
            if (dzWorkpieceData == null) {
                log.warn("交换码绑定的数据不存在:{}", authCode);
                return null;
            }
            dzWorkpieceData.setQrCode(qrCode);
            dzWorkpieceDataMapper.updateById(dzWorkpieceData);
            return Result.ok(dzWorkpieceData);
        } else {
            log.warn("检测数据 绑定二维码队列,指令不准确:{}", tcp);
        }

        return null;
    }

    @Override
    public DzWorkpieceData getQrCodeProduct(String qrCode) {
        List<DzWorkpieceData> qrCodeProduct = baseMapper.getQrCodeProduct(qrCode);
        if (CollectionUtils.isNotEmpty(qrCodeProduct)) {
            for (int i = 0; i < qrCodeProduct.size(); i++) {
                DzWorkpieceData dzWorkpieceData1 = qrCodeProduct.get(i);
                long dzWorkpieceDataI = dzWorkpieceData1.getDetectorTime().getTime();
                for (int i1 = 1; i1 < qrCodeProduct.size(); i1++) {
                    DzWorkpieceData dzWorkpieceData = qrCodeProduct.get(i1);
                    long dzWorkpieceDataJ = dzWorkpieceData.getDetectorTime().getTime();
                    if (dzWorkpieceDataJ > dzWorkpieceDataI) {
                        qrCodeProduct.set(i, dzWorkpieceData);
                        qrCodeProduct.set(i1, dzWorkpieceData1);
                    }
                }
            }
            return qrCodeProduct.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, List<CheckItems>> getProductIdCheckItems(String productId, String orderId, String lineId) {
        List<CheckItems> productIdCheckItems = baseMapper.getProductIdCheckItems(productId,orderId,lineId);
        if (CollectionUtils.isNotEmpty(productIdCheckItems)) {
            Map<String, List<CheckItems>> listMap = new HashMap<>();
            for (CheckItems items : productIdCheckItems) {
                String stationId = items.getStationId();
                List<CheckItems> checkItems = listMap.get(stationId);
                if (CollectionUtils.isNotEmpty(checkItems)) {
                    checkItems.add(items);
                } else {
                    checkItems = new ArrayList<>();
                    checkItems.add(items);
                    listMap.put(stationId, checkItems);
                }
            }
            return listMap;
        }
        return null;
    }



    @Override
    public List<Map<String, String>> getNewestThreeDataId(String orderNo, String lineNo, String modelNumber, int size) {
        return baseMapper.getNewestThreeDataId(orderNo,lineNo,modelNumber,size);
    }

    @Override
    public List<Map<String, String>> getNewestThreeDataIdQrcode(String orderNo, String lineNo, String modelNumber, int size, boolean qrCode) {
        return baseMapper.getNewestThreeDataIdQrcode(orderNo,lineNo,modelNumber,size,qrCode);
    }

    @Override
    public DzWorkpieceData getLastDzWorkpieceData(String orderNo, String lineNo, String now) {
        return dzWorkpieceDataMapper.getLastDzWorkpieceData(orderNo,lineNo,now);
    }
}
