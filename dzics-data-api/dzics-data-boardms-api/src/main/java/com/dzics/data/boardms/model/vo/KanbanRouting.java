package com.dzics.data.boardms.model.vo;

import com.dzics.data.common.base.enums.MenuType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "SysKanbanRouting对象", description = "菜单权限表")
public class KanbanRouting implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "菜单标题")
    private String title;

    /**
     * 路由序号
     */
    @ApiModelProperty(value = "路由序号")
    private String pathNumber;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "请求类型1GET2HEAD3POST4PUT5PATCH6DELETE7OPTIONS8TRACE")
    private String requestMethod;

    @ApiModelProperty(value = "组件")
    private String component;

    @ApiModelProperty(value = "组件名字")
    private String name;

    @ApiModelProperty(value = "面包屑点击控制noRedirect不可点击")
    private String redirect;

    @ApiModelProperty(value = "菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)")
    private MenuType menuType;


    @ApiModelProperty(value = "菜单权限编码")
    private String perms;

    @ApiModelProperty(value = "权限策略1显示2禁用")
    private String permsType;

    @ApiModelProperty(value = "菜单排序")
    private Double sortNo;

    @ApiModelProperty(value = "聚合子路由1是0否")
    private Boolean alwaysShow;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "是否路由菜单0不是1是默认值1")
    private Boolean isRoute;

    @ApiModelProperty(value = "是否缓存该页面1是0不是")
    private Boolean keepAlive;

    @ApiModelProperty(value = "是否隐藏路由0否1是")
    private Integer hidden;

    @ApiModelProperty(value = "外链菜单打开方式0内部打开1外部打开")
    private Boolean internalOrExternal;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "按钮权限状态0无效1有效")
    private Integer status;

    @ApiModelProperty(value = "删除状态0正常1已删除")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("产线序号")
    private String lineNo;

}
