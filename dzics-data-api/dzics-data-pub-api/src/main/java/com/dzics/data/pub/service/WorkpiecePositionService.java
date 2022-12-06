package com.dzics.data.pub.service;

import com.dzics.data.common.base.model.dto.position.SendPosition;

/**
 * @Classname WorkpiecePositionService
 * @Description 报工发送接口
 * @Date 2022/3/16 12:31
 * @Created by NeverEnd
 */
public interface WorkpiecePositionService {

    /**
     * 报工二维码发送到看板
     * @param qrcode
     * @return
     */
     boolean sendWorkpiecePosition(SendPosition qrcode);
}
