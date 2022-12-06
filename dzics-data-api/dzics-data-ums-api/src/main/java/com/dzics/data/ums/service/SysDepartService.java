package com.dzics.data.ums.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.ums.model.dao.SwitchSiteDo;
import com.dzics.data.ums.model.dto.depart.AddDepart;
import com.dzics.data.ums.model.dto.depart.DisableEnabledDepart;
import com.dzics.data.ums.model.dto.depart.SelDepart;
import com.dzics.data.ums.model.dto.depart.UpdateDepart;
import com.dzics.data.ums.model.entity.SysDepart;
import com.dzics.data.ums.model.vo.depart.ResDepart;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 站点公司表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysDepartService extends IService<SysDepart> {


    /**
     * @return 排除大正站点 所有站点
     */
    List<SysDepart> listNotDz();

    /**
     * @param addDepart 站点信息
     * @param sub       操作用户
     * @return
     */
    @CacheEvict(cacheNames = {"businessDepartService.listAll","businessUserService.querySwitchSite"},allEntries = true)
    Result addDepart(AddDepart addDepart, String sub);

    /**
     * @param pageLimit 分页信息
     * @param selDepart 查询站点条件信息
     * @param sub       操作用户
     * @return
     */
    Result<List<ResDepart>> queryDepart(PageLimit pageLimit, SelDepart selDepart, String sub);
    /**
     * 站点管理 Excel导出
     * */
    Result<List<ResDepart>> queryDepart(PageLimitBase pageLimit, SelDepart selDepart, String sub);

    /**
     * 添加站点
     *
     * @param sub      操作用户
     * @param departId 站点id
     * @return
     */
    Result getDepartMsg(String sub, Long departId);




    /**
     * @param sub      操作用户
     * @param departId 站点id
     * @return
     */
    Result getDepartDetails(String sub, Long departId);


    /**
     * @param updateDepart 更新的站点信息
     * @param sub          操作用户
     *                     1.清楚所有缓存
     *                     2.清除用户路由信息
     *                     3.清除权限角色信息
     *                     4.清除授权权限信息
     *                     5.清除站点信息
     *                     6.清除站点编码查询信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getRouters", "businessUserService.getInfo",
            "userRoleService.listRoleCode",
            "rolePermissionService.listRolePermissionCode", "businessDepartService.getById",
            "departService.getByCode","userRoleService.listOrgCodeBasicsRole",
            "rolePermissionService.listRolePermission","businessUserService.querySwitchSite"}, allEntries = true)
    Result updateDepart(UpdateDepart updateDepart, String sub);

    /**
     * 启用禁用站点
     *
     * @param enabledDepart 禁用启用信息
     * @param sub           操作用户
     *                      1.清楚所有缓存
     *                      2.清除用户路由信息
     *                      3.清除站点id查询信息
     *                      4.清除站点编码查询信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getRouters", "businessUserService.getInfo",
            "businessDepartService.getById","departService.getByCode"
            ,"rolePermissionService.listRolePermission","businessUserService.querySwitchSite"}, allEntries = true)
    Result disableEnabledRole(DisableEnabledDepart enabledDepart, String sub);

    /**
     * @param id 站点id
     * @return
     */
    @Cacheable(value = "businessDepartService.getById", key = "#id")
    SysDepart getById(Long id);

    /**
     * @param depQwp
     * @return
     */
    List<SysDepart> list(QueryWrapper<SysDepart> depQwp);

    List<SwitchSiteDo> listId(List<String> ids);

    /**
     * @return 获取所有站点
     */
    @Cacheable(value = "businessDepartService.listAll")
    List<SwitchSiteDo> listAll();

}
