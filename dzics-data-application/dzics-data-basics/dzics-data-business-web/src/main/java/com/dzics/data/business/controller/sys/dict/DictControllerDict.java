package com.dzics.data.business.controller.sys.dict;


import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.SysDict;
import com.dzics.data.pub.service.SysDictService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"字典数据相关"}, produces = "字典数据相关接口")

@RequestMapping("/dicts")
@RestController
public class DictControllerDict {

    @Autowired
    private SysDictService dictService;

    @ApiOperation(value = "根据id查询字典类型")
    @ApiOperationSupport(author = "jq", order = 5)
    @GetMapping(value = "/{id}")
    public Result<SysDict> selectDictById(@PathVariable("id") @ApiParam(value = "字典id(必填)", required = true) String id) {
        return dictService.selectDictById(id);
    }
}
