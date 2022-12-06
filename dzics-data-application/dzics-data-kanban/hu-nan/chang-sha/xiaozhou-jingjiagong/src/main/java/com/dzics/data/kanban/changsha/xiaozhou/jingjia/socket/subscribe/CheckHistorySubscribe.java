package com.dzics.data.kanban.changsha.xiaozhou.jingjia.socket.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.constant.ProductDefault;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.util.SubscribeUtil;
import com.dzics.data.kanban.changsha.xiaozhou.jingjia.model.proto.XiaoZhouJingKanBanProtobuf;
import com.dzics.data.kanban.changsha.xiaozhou.jingjia.model.vo.ProDetectionXiaoZhouJingJia;
import com.dzics.data.kanban.changsha.xiaozhou.jingjia.socket.server.SocketIoHandler;
import com.dzics.data.pdm.model.dao.DzWorkpieceDataDo;
import com.dzics.data.pms.model.dto.TableColumnDto;
import com.dzics.data.pub.service.kanban.DetectorDataService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Classname CheckHistorySubscribe
 * @Description 描述
 * @Date 2022/3/17 17:25
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class CheckHistorySubscribe {
    private RedisUtil redisUtil;
    private SocketIoHandler socketIoHandler;
    private DetectorDataService<ProDetectionXiaoZhouJingJia<List<DzWorkpieceDataDo>>> workpieceDataService;

    public CheckHistorySubscribe(RedisUtil redisUtil, SocketIoHandler socketIoHandler, DetectorDataService<ProDetectionXiaoZhouJingJia<List<DzWorkpieceDataDo>>> workpieceDataService) {
        this.redisUtil = redisUtil;
        this.socketIoHandler = socketIoHandler;
        this.workpieceDataService = workpieceDataService;
    }

    /**
     * 订阅检测项记录
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.TEST_ITEM_RECORD)
    public void getTestItemRecord(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 检测项记录 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            //        检测数据
            String modelNumber = (String) redisUtil.get(RedisKey.CHECK_PRODUCT + orderNo + lineNo);
            if (StringUtils.isEmpty(modelNumber)) {
                modelNumber = ProductDefault.modelNumber;
            }
            ProDetectionXiaoZhouJingJia<List<DzWorkpieceDataDo>> detectorData = workpieceDataService.getDetectorData(orderNo, lineNo, modelNumber);
            if (detectorData != null) {
                List<DzWorkpieceDataDo> tableData = detectorData.getTableData();
                List<TableColumnDto> tableColumn = detectorData.getTableColumn();
                XiaoZhouJingKanBanProtobuf.CheckProductBase.Builder builder = XiaoZhouJingKanBanProtobuf.CheckProductBase.newBuilder();
                builder.setType(DeviceSocketSendStatus.FOUR_PRODUCT_TEST_DATA.getInfo());
                XiaoZhouJingKanBanProtobuf.ProDetectionAnren.Builder builder1 = XiaoZhouJingKanBanProtobuf.ProDetectionAnren.newBuilder();
                for (DzWorkpieceDataDo tableDatum : tableData) {
                    XiaoZhouJingKanBanProtobuf.TableData.Builder builder2 = XiaoZhouJingKanBanProtobuf.TableData.newBuilder();
                    builder2.setDetect01(tableDatum.getDetect01());
                    builder2.setDetect02(tableDatum.getDetect02());
                    builder2.setDetect03(tableDatum.getDetect03());
                    builder2.setDetect04(tableDatum.getDetect04());
                    builder2.setDetect05(tableDatum.getDetect05());
                    builder2.setDetect06(tableDatum.getDetect06());
                    builder2.setDetect07(tableDatum.getDetect07());
                    builder2.setDetect08(tableDatum.getDetect08());
                    builder2.setDetect09(tableDatum.getDetect09());
                    builder2.setDetect10(tableDatum.getDetect10());
                    builder2.setDetect11(tableDatum.getDetect11());
                    builder2.setDetect12(tableDatum.getDetect12());
                    builder2.setDetect13(tableDatum.getDetect13());
                    builder2.setDetect14(tableDatum.getDetect14());
                    builder2.setDetect15(tableDatum.getDetect15());
                    builder2.setDetect16(tableDatum.getDetect16());
                    builder2.setDetect17(tableDatum.getDetect17());
                    builder2.setDetect18(tableDatum.getDetect18());
                    builder2.setDetect19(tableDatum.getDetect19());
                    builder2.setDetect20(tableDatum.getDetect20());
                    builder2.setDetect21(tableDatum.getDetect21());
                    builder2.setDetect22(tableDatum.getDetect22());
                    builder2.setDetect23(tableDatum.getDetect23());
                    builder2.setDetect24(tableDatum.getDetect24());
                    builder2.setDetect25(tableDatum.getDetect25());
                    builder2.setDetect26(tableDatum.getDetect26());
                    builder2.setDetect27(tableDatum.getDetect27());
                    builder2.setDetect28(tableDatum.getDetect28());
                    builder2.setProducBarcode(tableDatum.getProducBarcode());
                    builder2.setName(tableDatum.getName());
                    builder2.setDetectorTime(tableDatum.getDetectorTime());
                    builder1.addTableData(builder2);
                }
                for (TableColumnDto tableColumnDto : tableColumn) {
                    XiaoZhouJingKanBanProtobuf.TableColumn.Builder builder2 = XiaoZhouJingKanBanProtobuf.TableColumn.newBuilder();
                    builder2.setColData(tableColumnDto.getColData());
                    builder2.setColName(tableColumnDto.getColName());
                    builder2.setExhibition(tableColumnDto.getExhibition());
                    builder1.addTableColumn(builder2);
                }
                builder.setData(builder1);
                byte[] bytes = builder.build().toByteArray();
                client.sendEvent(SocketMessageType.TEST_ITEM_RECORD, bytes);
            }
            socketIoHandler.addEvent(client, SocketMessageType.TEST_ITEM_RECORD + orderNo + lineNo, "检测项记录");
            long b = System.currentTimeMillis();
            log.info("订阅检测项记录,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消订阅检测项记录
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.UN_TEST_ITEM_RECORD)
    public void unGetTestItemRecord(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅检测项 ] 记录:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.TEST_ITEM_RECORD + orderNo + lineNo, "检测项记录");
    }
}
