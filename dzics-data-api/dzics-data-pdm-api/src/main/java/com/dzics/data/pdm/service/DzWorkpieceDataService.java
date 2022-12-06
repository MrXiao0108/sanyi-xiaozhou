package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.model.dto.CheckItems;
import com.dzics.data.pdm.model.entity.DzWorkpieceData;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备检测数据V2新版记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface DzWorkpieceDataService extends IService<DzWorkpieceData> {
    /**
     * 检测数据 绑定二维码
     * @param rabbitmqMessage
     * @return
     */
    Result processingData(RabbitmqMessage rabbitmqMessage);
    /**
     * 根据二维码获取检测 数据 和关联的产品
     * 获取最新的一条检测记录
     * @param qrCode
     * @return
     */
    DzWorkpieceData getQrCodeProduct(String qrCode);

    /**
     * 根据产品id 获取 该 产品所绑定 工位的 检测项
     * @param productId
     * @param orderId
     * @param lineId
     * @return
     */
    Map<String, List<CheckItems>>   getProductIdCheckItems(String productId, String orderId, String lineId);


    List<Map<String, String>> getNewestThreeDataId(String orderNo, String lineNo, String modelNumber, int size);

    List<Map<String, String>> getNewestThreeDataIdQrcode(String orderNo, String lineNo, String modelNumber, int size,boolean qrCode);

    DzWorkpieceData getLastDzWorkpieceData(String orderNo, String lineNo, String now);
}
