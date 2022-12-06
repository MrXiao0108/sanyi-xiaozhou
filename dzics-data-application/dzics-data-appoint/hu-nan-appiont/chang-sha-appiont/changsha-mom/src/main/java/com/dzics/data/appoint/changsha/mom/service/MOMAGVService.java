package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.EmptyFrameMovesDzdc;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.MOMAGVHandshakeDto;
import com.dzics.data.appoint.changsha.mom.model.vo.MomQrCodeVo;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.common.base.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * MOM AGV 服务
 *
 * @author: van
 * @since: 2022-07-07
 */
public interface MOMAGVService {

    /**
     * @param emptyFrameMovesDzdc: DTO
     * @return 处理结果
     * @author van
     * @date 2022/7/7
     */
    Result<String> callAgv(EmptyFrameMovesDzdc emptyFrameMovesDzdc);

    /**
     * 生产叫料
     *
     * @param frameMoves:
     * @return 处理结果
     * @author van
     * @date 2022/7/7
     */
    Result<String> callBoxMaterial(EmptyFrameMovesDzdc frameMoves);

    /**
     * 叫空料框
     *
     * @param frameMoves:
     * @return 处理结果
     * @author van
     * @date 2022/7/7
     */
    Result<String> callEmptyBox(EmptyFrameMovesDzdc frameMoves);

    /**
     * 移出满料框
     *
     * @param frameMoves:
     * @return 处理结果
     * @author van
     * @date 2022/7/7
     */
    Result<String> exportFullBox(EmptyFrameMovesDzdc frameMoves);

    /**
     * 工序间配送，拉料 和 送空料框
     *
     * @param emptyFrameMovesDzdc: 报文接口DTO
     * @return 处理结果
     * @author van
     * @date 2022/7/7
     */
    Result processDistribution(EmptyFrameMovesDzdc emptyFrameMovesDzdc);

    /**
     * 空料框移出
     *
     * @param frameMoves:
     * @return 处理结果
     * @author van
     * @date 2022/7/7
     */
    Result<String> removeEmptyBox(EmptyFrameMovesDzdc frameMoves);

    /**
     * MOM AGV握手请求处理
     *
     * @param information: 请求内容
     * @return 处理结果
     * @author van
     * @date 2022/8/1
     */
    ResultDto handshakeHandle(IssueOrderInformation<MOMAGVHandshakeDto> information);

    /**
     * 18号厂房 agv握手处理
     *
     * @param state: 状态 Agv2rasterController.Status
     * @return 处理结果
     * @author van
     * @date 2022/8/14
     */
    Map<String, Object> handshakeHandle(String state);

    /**
     * MOM AGV握手反馈
     *
     * @author van
     * @date 2022/8/1
     */
    void handshakeOk();

    /**
     * MOM AGV握手反馈处理
     *
     * @author van
     * @date 2022/8/1
     */
    void handshakeResult();

    /**
     * MOM AGV握手 机器人相关处理
     *
     * @param str: 请求报文
     * @return 响应报文
     * @author van
     * @date 2022/8/1
     */
    String handshakeTcpHandle(String str);


    /**
     * Mom 装框信息
     *
     * @param serialNos 序列号信息
     * @param ContainerListNo 料框编码
     * @param pointModel 料点模式
     */
    void packingComplete(List<MomQrCodeVo> serialNos, String ContainerListNo, String pointModel,String point);
}
