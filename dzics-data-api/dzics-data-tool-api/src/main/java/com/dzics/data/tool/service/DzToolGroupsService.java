package com.dzics.data.tool.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.tool.model.dto.AddDzToolGroupVo;
import com.dzics.data.tool.model.dto.PutToolGroupsVo;
import com.dzics.data.tool.model.entity.DzToolGroups;

import java.util.List;

/**
 * <p>
 * 刀具组表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface DzToolGroupsService extends IService<DzToolGroups> {
    /**
     * 查询刀具组
     * @param sub
     * @param pageLimit
     * @param groupNo
     * @param useOrgCode
     * @return
     */
    Result<List<DzToolGroups>> getToolGroupsList(String sub, PageLimit pageLimit, String groupNo, String useOrgCode);

    /**
     * 添加刀具组
     * @param addDzToolGroupVo
     * @param orgCode
     * @return
     */
    Result addToolGroups(AddDzToolGroupVo addDzToolGroupVo, String orgCode);
    /**
     * 删除刀具组
     * @param toolGroupsId
     * @return
     */
    Result delToolGroups(String toolGroupsId);


    /**
     * 修改刀具组编号
     * @param putToolGroupsVo
     * @return
     */
    Result putToolGroups(PutToolGroupsVo putToolGroupsVo);

    /**
     * 根据刀具组id查询刀具列表
     * @param toolGroupsId
     * @param pageLimit
     * @return
     */
    Result getToolInfoList(Long toolGroupsId, PageLimit pageLimit);


    /**
     * 删除刀具判断
     * @param id
     * @return
     */
    Result delToolInfo(Long id);

    /**
     * 编辑刀具号
     * @param id
     * @param toolNo
     * @return
     */
    Result putToolInfo(Long id, Integer toolNo);


    /**
     * 新增刀具组id
     * @param toolGroupId
     * @param toolNo
     * @return
     */
    Result addToolInfo(String toolGroupId, Integer toolNo);
}
