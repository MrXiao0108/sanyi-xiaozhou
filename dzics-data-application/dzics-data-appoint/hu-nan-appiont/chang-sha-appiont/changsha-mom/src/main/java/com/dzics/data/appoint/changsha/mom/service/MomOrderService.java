package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dao.MomOrderDo;
import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.PutMomOrder;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchOrderParms;
import com.dzics.data.appoint.changsha.mom.model.dto.Task;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.common.base.vo.Result;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * mom下发订单表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
public interface MomOrderService extends IService<MomOrder> {

    /**
     * Mom订单下发
     *
     * @param requestHeaderVo：json格式转换实体类
     * @param momParams：订单下发报文
     * @return ResultDto
     */
    ResultDto saveMomOrder(IssueOrderInformation<Task> requestHeaderVo, String momParams);

    /**
     * 更新订单号获取订单
     *
     * @param wipOrderNo：Mom订单编号
     * @param orderNo：产线订单编号
     * @param lineNo：产线编号
     * @return List<MomOrder>：MomOrder集合
     */
    List<MomOrder>getWipOrderNo(String wipOrderNo, String orderNo, String lineNo);

    /**
     * -----------------
     *
     * @param wipOrderNo：Mom订单编号
     * @param orderId：订单ID
     * @param lineId：产线ID
     * @return MomOrder
     */
    MomOrder getMomOrder(String wipOrderNo, String orderId, String lineId);

    /**
     * 根据订单状态获取订单 ID 和订单号
     *
     * @param orderNo:
     * @param lineNo:
     * @param loading:
     * @return 查询结果
     */
    MomOrder getByLoading(String orderNo, String lineNo, String loading);

    /**
     * 正在执行中的订单状态
     *
     * @param orderId:
     * @param lineId:
     * @param operationResultLoading:
     * @return :
     */
    MomOrder getOrderOperationResult(String orderId, String lineId, String operationResultLoading);

    /**
     *----------------------
     *
     * @param parms
     * @return Result<List<MomOrderDo>>
     */
    Result<List<MomOrderDo>> listQuery(SearchOrderParms parms);

    /**
     * 切换订单
     *
     * @param proTaskOrderId: mom下发订单表 ID
     * @author van
     * @date 2022/6/27
     */
    void changeOrder(String proTaskOrderId);

    /**
     * 订单开始
     *
     * @param proTaskOrderId: MOM订单主键
     * @author van
     * @date 2022/6/30
     */
//    void orderStart(String proTaskOrderId);

    /**
     * 通知产线订单开始
     *
     * @param oldOrder: MOM订单实体
     * @param newOrder: MOM订单实体1
     * @param equipmentIds
     * @author van
     * @date 2022/7/6
     */
    void orderStart(MomOrder oldOrder,MomOrder newOrder,List<String> equipmentIds,String type);

    /**
     * 通知产线订单关闭
     *
     * @param momOrder: MOM订单实体
     * @author van
     * @date 2022/7/12
     */
    void orderClose(MomOrder momOrder,MomOrder momOrder1,String type);

    /**
     * 订单暂停
     *
     * @param momOrder: MOM订单实体
     * @author van
     * @date 2022/7/12
     */
    void orderStop(MomOrder momOrder,MomOrder momOrder1,String type);

    /**
     * 订单恢复
     *
     * @param momOrder: MOM订单实体
     * @author van
     * @date 2022/7/12
     */
    void orderRecover(MomOrder momOrder,MomOrder momOrder1,String type);

    /**
     * 订单开始前初始话处理
     *
     * @param momOrder: MOM订单实体
     * @param equipmentIds
     * @return 订单开始前初始话处理结果
     * @author van
     * @date 2022/7/6
     */
    boolean orderStartInit(MomOrder momOrder, List<String> equipmentIds);

    /**
     * ------------
     * @param putMomOrder
     * @return Result
     */
    Result orderStart(PutMomOrder putMomOrder);

    /**
     * 订单暂停
     *
     * @param putMomOrder
     * @return Result
     */
    Result orderStop(PutMomOrder putMomOrder);

    /**
     * 强制关闭订单
     *
     * @param putMomOrder
     * @return
     */
    Result forceClose(PutMomOrder putMomOrder);

    /**
     * 订单恢复
     *
     * @param putMomOrder
     * @return
     */
    Result orderRecover(PutMomOrder putMomOrder);


    /**
     * 订单作废
     *
     * @param proTaskOrderId
     * @return Result
     */
    Result orderDelete(String proTaskOrderId);


    /**
     * 物料详情
     *
     * @param proTaskOrderId
     * @return Result
     */
    Result orderMaterialDetail(String proTaskOrderId);

    /**
     * 向产线发送订单开始通知
     * @param oldOrder:  MOM旧订单实体
     * @param momOrder:  MOM新订单实体
     * @param dzUdpType: 消息类型
     * @param type: 加工区域
     * @return 发送结果
     * @author van
     * @date 2022/7/6
     */
    boolean sendMessageForRobot(MomOrder oldOrder,MomOrder momOrder, String dzUdpType,String type);

    /**
     * 更新订单生产数量
     *
     * @param momOrder
     * @param wipOrderNo
     * @param quantity
     * @return MomOrder
     */
    @Transactional(rollbackFor=Exception.class,isolation = Isolation.SERIALIZABLE)
    MomOrder updateQuantity(MomOrder momOrder, String wipOrderNo, String quantity);

    /**
     * 跟新订单状态
     *
     * @param momOrder:
     * @param value:
     * @return MonOrder 跟新订单后的信息返回
     */
    MomOrder updateOrderStates(MomOrder momOrder, String value);

    /**
     * -----------------
     *  订单转发
     * @param proTaskOrderId
     * @return Result
     */
    Result forwardOrder(String proTaskOrderId);

    /**
     * 获取当前加工中心正在变更中的订单
     * @param orderId
     * @param lineId
     * @param workStation
     * @return MomOrder
     */
    MomOrder getWorkingOrder(String orderId,String lineId,String workStation);

    /**
     *  更新订单抓取数量
     * @param orderNo：订单编号
     * @param lineNo：产线编号
     * @param monOrderSel：MomOrder订单
     * @param sum：数量
     */
    void updateOrderStateSum(String orderNo, String lineNo, MomOrder monOrderSel, Integer sum);


    /**
     * 跟新订单生产数量
     *
     * @param monOrderSel
     * @param wipOrderno
     * @param quantity
     * @return MomOrder
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    MomOrder upDateQuantity(MomOrder monOrderSel, String wipOrderno, String quantity);


    /**
     * 自动开始订单
     *
     * @param momOder
     * @param orderNo
     * @param lineNo
     */
    void startMomOrder(MomOrder momOder, String orderNo, String lineNo);


    /**
     * 自动匹配查找，相同物料、已下发、无状态变更的Mom订单
     *
     * @param orderId 订单ID
     * @param lineId  产线ID
     * @param productNo 物料编码
     * @param down 订单状态110
     * @return MomOrder
     */
    MomOrder getOrderCallMaterialStatus(String orderId, String lineId, String productNo, String down);

}
