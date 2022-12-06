package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.vo.DNCProgramVo;
import com.dzics.data.appoint.changsha.mom.model.vo.DncLogVo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzEquipment;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * dnc 换型信息 服务类
 * </p>
 *
 * @author van
 * @since 2022-06-28
 */
public interface DncProgramService extends IService<DncProgram> {

    /**
     * 当前运行的程序
     *
     * @return 当前运行的程序
     * @author van
     * @date 2022/6/28
     */
    DncProgram currentProgram();

    /**
     * 切换程序
     *
     * @param momOrder: MOM订单实体
     * @param isRepeat: 是人工操作
     * @author van
     * @date 2022/6/28
     */
    boolean changeProgram(MomOrder momOrder, boolean isRepeat, List<DzEquipment> equipments);

    /**
     * 取消（状态修改为人工干预）
     *
     * @param id: 主键
     * @return 处理结果
     * @author van
     * @date 2022/7/4
     */
    Result<String> cancel(String id);

    /**
     * 当前运行设备
     *
     * @param dzOrder:     大正订单号
     * @param equipmentNo: 设备序号
     * @return 目标数据
     * @author van
     * @date 2022/7/4
     */
    DncProgram currentProgram(String dzOrder, String equipmentNo);

    /**
     * 响应当前设备程序号
     *
     * @param map: 参数实体
     * @author van
     * @date 2022/7/4
     */
    DncProgram dncProgramHandel(Map<String, String> map);

    /**
     * 订单生产前 程序初始化处理
     *
     * @param momOrder: MOM订单
     * @return true: 程序准备就绪
     * @author van
     * @date 2022/6/28
     */
    boolean initProgram(MomOrder momOrder, List<String> equipmentIds);

    /**
     * 输入主程序号
     *
     * @param dncProgramVo: 主键
     * @return 干预结果
     * @author van
     * @date 2022/6/30
     */
    Result<String> manualIntervention(DNCProgramVo dncProgramVo);

    /**
     * 分页列表
     *
     * @param dncProgramVo: 查询条件
     * @return 查询结果
     * @author van
     * @date 2022/6/29
     */
    Result<List<DNCProgramVo>> page(DNCProgramVo dncProgramVo);

    /**
     * 程序重发
     *
     * @param id: 主键
     * @return 重发结果
     * @author van
     * @date 2022/6/30
     */
    Result<String> repeat(String id);

    /**
     * 新增
     *
     * @param dncProgram: 实体
     * @author van
     * @date 2022/6/28
     */
    void saveProgram(DncProgram dncProgram);

    /**
     * 向DNC发送消息
     *
     * @param dncProgram: 实体
     * @author van
     * @date 2022/7/1
     * @return boolean
     */
    boolean sendProgram(DncProgram dncProgram);


    /**
     * 查询Dnc交互日志
     *
     * @param dncLogVo: 实体
     * @return List<DncProgram>：集合
     * @date 2022/7/1
     */
    List<DncProgram>getDncLog(DncLogVo dncLogVo) throws ParseException;
}
