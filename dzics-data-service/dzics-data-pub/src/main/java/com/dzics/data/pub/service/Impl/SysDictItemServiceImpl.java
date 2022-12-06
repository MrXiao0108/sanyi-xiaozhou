package com.dzics.data.pub.service.Impl;



import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.pub.db.dao.SysDictDao;
import com.dzics.data.pub.db.dao.SysDictItemDao;
import com.dzics.data.pub.model.dto.DictItemVo;
import com.dzics.data.pub.model.entity.SysDict;
import com.dzics.data.pub.model.entity.SysDictItem;
import com.dzics.data.pub.service.SysDictItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统字典详情 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
@Slf4j
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemDao, SysDictItem> implements SysDictItemService {
    @Autowired
    SysDictDao sysDictMapper;
    @Autowired
    private SysDictItemDao sysDictItemMapper;

    @Override
    public String getIndexIsShowNg() {
        try {
            SysDictItem dict_code = getOne(new QueryWrapper<SysDictItem>().eq("dict_code", "index_is_show_ng_ok"));
            if (dict_code == null) {
                return "false";
            }
            return dict_code.getItemValue();
        } catch (Throwable e) {
            log.error("获取是否在首页展示NG异常:{}", e.getMessage(), e);
            return "false";
        }
    }
    @Override
    public String getSystemConfigDepart() {
        try {
            SysDictItem dict_code = getOne(new QueryWrapper<SysDictItem>().eq("dict_code", "sys_depart"));
            if (dict_code == null) {
                return "SANY";
            }
            return dict_code.getItemText();
        } catch (Exception e) {
            return "SANY";
        }
    }

    @Override
    public String getDictTest(String datasource, String key) {
        return sysDictItemMapper.getDictTest(datasource, key);
    }
    @Override
    public Result addDictItem(String sub, DictItemVo dictItemVo) {
        //字典类型是存在判断
        SysDict sysDict = sysDictMapper.selectById(dictItemVo.getDictId());
        if(sysDict==null){
            log.error("字典类型id不存在:{}",dictItemVo.getDictId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_13);
        }
        List<SysDictItem> itemText = sysDictItemMapper.selectList(new QueryWrapper<SysDictItem>().eq("dict_id",dictItemVo.getDictId()).eq("item_text", dictItemVo.getItemText()));
        if(itemText.size()>0){
            log.error("字典项文本已存在:{}",dictItemVo.getItemText());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_14);
        }
        List<SysDictItem> itemValue = sysDictItemMapper.selectList(new QueryWrapper<SysDictItem>().eq("dict_id",dictItemVo.getDictId()).eq("item_value", dictItemVo.getItemValue()));
        if(itemValue.size()>0){
            log.error("字典项值已存在:{}",dictItemVo.getItemValue());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_15);
        }
        SysDictItem sysDictItem=new SysDictItem();
        BeanUtils.copyProperties(dictItemVo,sysDictItem);
        sysDictItem.setDictCode(sysDict.getDictCode());
        sysDictItemMapper.insert(sysDictItem);
        return new Result(CustomExceptionType.OK,sysDictItem);
    }

    @Override
    public Result delDictItem(String sub, String id) {
        int i = sysDictItemMapper.deleteById(id);
        if(i>0){

            return new Result(CustomExceptionType.OK,Message.OK_2);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_6);
    }

    @Override
    public Result updateDictItem(String sub, DictItemVo dictItemVo) {
        if(dictItemVo.getId()==null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_5);
        }
        SysDictItem sysDictItem = sysDictItemMapper.selectById(dictItemVo.getId());
        if(sysDictItem==null){
            log.error("数据字典值id不存在:{}",dictItemVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_6);
        }
        sysDictItem.setItemValue(dictItemVo.getItemValue());
        sysDictItem.setItemText(dictItemVo.getItemText());
        sysDictItem.setDescription(dictItemVo.getDescription());
        sysDictItem.setSortOrder(dictItemVo.getSortOrder());
        int i = sysDictItemMapper.updateById(sysDictItem);
        if(i>0){
            return new Result(CustomExceptionType.OK,sysDictItem);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_3);

    }

    @Override
    public Result<SysDictItem> listDictItem(PageLimit pageLimit, String dictId) {
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>().eq("dict_id", dictId);
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        List<SysDictItem> dictItems = sysDictItemMapper.selectList(wrapper);
        return new Result(CustomExceptionType.OK,dictItems);
    }

    @Override
    public Result<SysDictItem> getDictItem(String dictCode) {
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>().eq("dict_code", dictCode);
        wrapper.orderByAsc("sort_order");
        List<SysDictItem> dictItems = sysDictItemMapper.selectList(wrapper);
        return new Result(CustomExceptionType.OK,dictItems);
    }

    @Override
    public Result<SysDictItem> getItemListByCode(String dictCode) {
        List<SysDictItem> dictItems = sysDictItemMapper.selectList(new QueryWrapper<SysDictItem>().eq("dict_code", dictCode));
        return new Result(CustomExceptionType.OK,dictItems);
    }

    @Override
    public String getMomRunModel(String momRunModelKey, String orderCode) {
        return sysDictItemMapper.getDictCodeAndItemText(momRunModelKey, orderCode);
    }

    @Override
    public String updateAgvRunModel(String momRunModelKey, String orderCode, Integer logId) {
        int update = 0;
        String rm = "";
        try {
            QueryWrapper<SysDictItem> wp = new QueryWrapper<>();
            wp.eq("dict_code", momRunModelKey);
            wp.eq("item_text", orderCode);
            rm = logId.intValue() == 1 ? "auto" : "manual";
            SysDictItem sysDictItem = new SysDictItem();
            sysDictItem.setItemValue(rm);
            update = sysDictItemMapper.update(sysDictItem, wp);
        }catch(Throwable throwable){
            throwable.printStackTrace();
        }
        return update > 0 ? rm : null;
    }

}
