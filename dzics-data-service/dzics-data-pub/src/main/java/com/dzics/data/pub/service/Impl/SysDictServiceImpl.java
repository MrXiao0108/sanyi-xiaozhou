package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.pub.db.dao.SysDictDao;
import com.dzics.data.pub.db.dao.SysDictItemDao;
import com.dzics.data.pub.model.entity.SysDict;
import com.dzics.data.pub.model.entity.SysDictItem;
import com.dzics.data.pub.model.vo.DictVo;
import com.dzics.data.pub.service.SysDictService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 系统字典表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDict> implements SysDictService {
    @Autowired
    SysDictDao sysDictMapper;
    @Autowired
    SysDictItemDao sysDictItemMapper;

    @Override
    public Result addDict(String sub, DictVo dictVo) {
        if(dictVo.getDictCode()==null||dictVo.getDictCode().equals("")){
            log.error("添加字典时,字典编码为空:"+dictVo.getDictCode());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_11);
        }
//        if(dictVo.getType()==null){
//            log.error("添加字典时,字典类型为空:"+dictVo.getType());
//            return new Msg(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.err12);
//        }
        Integer i = sysDictMapper.hasDict(dictVo);
        if(i!=null&&i>0){
            log.error("字典名称或字典编码重复,名称:"+dictVo.getDictName()+",编码:"+dictVo.getDictCode());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_10);
        }
        SysDict dict=new SysDict();
        BeanUtils.copyProperties(dictVo,dict);
        dict.setId(null);
        sysDictMapper.insert(dict);
        return new Result(CustomExceptionType.OK,dict);

    }

    @Override
    public Result delDict(String sub, Integer id) {
        SysDictItem sysDictItem = new SysDictItem();
        sysDictItem.setStatus(1);
        sysDictItemMapper.update(sysDictItem,new QueryWrapper<SysDictItem>().eq("dict_id",id));
        int i = sysDictMapper.deleteById(id);
        if(i>0){
            return new Result(CustomExceptionType.OK,Message.OK_2);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_6);
    }

    @Override
    public Result updDict(String sub, DictVo dictVo) {
        if(dictVo.getId()==null){
            log.error("修改字典类型,字典类型id为空:"+dictVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        SysDict sysDict = sysDictMapper.selectById(dictVo.getId());
        if(sysDict==null){
            log.error("修改字典类型,id不存在:{}",dictVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_6);
        }
        sysDict.setDictName(dictVo.getDictName());
        sysDict.setDescription(dictVo.getDescription());
        sysDictMapper.updateById(sysDict);
        return new Result(CustomExceptionType.OK,sysDict);
    }


    @Override
    public Result selectDictById(String id) {
        SysDict sysDict = sysDictMapper.selectById(id);
        if(sysDict==null){
            log.error("修改字典类型,id不存在:{}",id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        return new Result(CustomExceptionType.OK,sysDict);
    }

    @Override
    public Result<List<SysDict>> listDict(PageLimitBase pageLimit, String dictName, String dictCode, String description) {
        QueryWrapper<SysDict> wrapper=new QueryWrapper();
        if(!StringUtils.isEmpty(dictName)){
            wrapper.like("dict_name",dictName);
        }
        if(!StringUtils.isEmpty(dictCode)){
            wrapper.like("dict_code",dictCode);
        }
        if(!StringUtils.isEmpty(description)){
            wrapper.like("description",description);
        }
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        }
        List<SysDict> sysDicts = sysDictMapper.selectList(wrapper);
        PageInfo<SysDict> info=new PageInfo<>(sysDicts);
        return new Result(CustomExceptionType.OK,info.getList(),info.getTotal());
    }
}
