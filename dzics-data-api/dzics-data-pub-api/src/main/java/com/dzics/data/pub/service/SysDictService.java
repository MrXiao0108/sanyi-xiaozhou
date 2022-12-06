package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.SysDict;
import com.dzics.data.pub.model.vo.DictVo;

import java.util.List;

/**
 * <p>
 * 系统字典表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysDictService extends IService<SysDict> {
    //新增字典类型
    Result addDict(String sub, DictVo dictVo);
    //删除字典类型
    Result delDict(String sub, Integer id);
    //修改字典类型
    Result updDict(String sub, DictVo dictVo);


    //根据id查询字典类型
    Result selectDictById(String id);
    //查询数据字典list
    Result<List<SysDict>> listDict(PageLimitBase pageLimit, String dictName, String dictCode, String description);
}
