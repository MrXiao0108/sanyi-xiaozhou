package com.dzics.data.boardms.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.boardms.model.dto.menu.AddPermission;
import com.dzics.data.boardms.model.dto.menu.SelKbRouting;
import com.dzics.data.boardms.model.dto.menu.UpdatePermission;
import com.dzics.data.boardms.model.entity.SysKanbanRouting;
import com.dzics.data.boardms.model.vo.MenusInfo;

/**
 * 看板相关路由接口信息
 *
 * @author ZhangChengJun
 * Date 2021/4/28.
 * @since
 */
public interface KanbanService extends IService<SysKanbanRouting> {
    /**
     * 采单列表
     * @param sub
     * @return
     */
    Result<MenusInfo> selMenuPermission(String sub);

    Result addPermission(AddPermission addPermission, String sub);

    Result<MenusInfo> selMenuPermissionId(Long id, String sub);

    Result updatePermission(UpdatePermission updatePermission, String sub);

    Result delPermission(Long id, String sub);

    /**
     * 看板路由详情
     * @param kbRouting
     * @param sub
     * @return
     */
    Result selRoutingDetails(SelKbRouting kbRouting, String sub);

    Result selRouting(String sub);

    /**
     * 路由节点
     * @param kbRouting
     * @param sub
     * @return
     */
    Result selMenuRouting(SelKbRouting kbRouting, String sub);

    /**
     * 根据path 获取订单信息
     * @param kbRouting
     * @param sub
     * @return
     */
    Result selRoutingDetailsOrder(SelKbRouting kbRouting, String sub);
}
