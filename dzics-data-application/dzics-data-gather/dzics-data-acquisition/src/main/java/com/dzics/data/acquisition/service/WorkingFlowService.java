package com.dzics.data.acquisition.service;

import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.dto.position.SendPosition;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

/**
 * 工作流程处理类
 *
 * @author ZhangChengJun
 * Date 2021/5/19.
 * @since
 */
@Service
public interface WorkingFlowService {

    /**
     * 处理工件位置数据
     *
     * @param rabbitmqMessage
     * @return
     */
    SendPosition processingData(RabbitmqMessage rabbitmqMessage);

    SendPosition workFlow(OrderIdLineId orderIdLineId, DzWorkStationManagement saMt, String timestamp, String qrCode, String outInputType, String deviceCode, DzWorkStationManagement workStationSpare);

    /**
     * 不存在该二维码则插入
     * @param qrCode
     * @param nowLocalDate
     * @param orderId
     * @param lineId
     */
    void saveBig(String qrCode, LocalDate nowLocalDate, Date workTime, String orderId, String lineId);
}
