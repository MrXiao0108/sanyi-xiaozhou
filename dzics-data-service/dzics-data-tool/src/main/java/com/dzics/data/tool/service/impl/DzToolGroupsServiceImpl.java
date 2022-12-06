package com.dzics.data.tool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.tool.db.dao.DzToolCompensationDataDao;
import com.dzics.data.tool.db.dao.DzToolGroupsDao;
import com.dzics.data.tool.db.dao.DzToolInfoDao;
import com.dzics.data.tool.model.dto.AddDzToolGroupVo;
import com.dzics.data.tool.model.dto.PutToolGroupsVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import com.dzics.data.tool.model.entity.DzToolGroups;
import com.dzics.data.tool.model.entity.DzToolInfo;
import com.dzics.data.tool.service.DzToolGroupsService;
import com.dzics.data.tool.service.DzToolInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 刀具组表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Service
@Slf4j
public class DzToolGroupsServiceImpl extends ServiceImpl<DzToolGroupsDao, DzToolGroups> implements DzToolGroupsService {

    @Autowired
    private DzToolInfoDao infoDao;
    @Autowired
    private DzToolCompensationDataDao compensationDataDao;
    @Autowired
    private DzToolInfoService dzToolInfoService;

    @Override
    public Result<List<DzToolGroups>> getToolGroupsList(String sub, PageLimit pageLimit, String groupNo, String useOrgCode) {
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<DzToolGroups> list = this.baseMapper.getToolGroupsList(pageLimit.getField(), pageLimit.getType(), groupNo, useOrgCode);

        List<String> ids = list
                .stream()
                .map(DzToolGroups::getToolGroupsId)
                .collect(Collectors.toList());
        Map<String, List<DzToolInfo>> map = dzToolInfoService.mapById(ids);
        list.forEach(m -> {
            List<DzToolInfo> toolInfos = map.get(m.getToolGroupsId());
            if (!ObjectUtils.isEmpty(toolInfos)) {
                m.setToolSum(toolInfos.size());
            }
        });

        PageInfo<DzToolGroups> pageInfo = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    @Transactional
    public Result addToolGroups(AddDzToolGroupVo addDzToolGroupVo, String orgCode) {
        List<DzToolGroups> groupNo = this.baseMapper.selectList(new QueryWrapper<DzToolGroups>().eq("group_no", addDzToolGroupVo.getGroupNo()));
        if (groupNo.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_100);
        }
        if (addDzToolGroupVo.getToolNoList() == null || addDzToolGroupVo.getToolNoList().size() == 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_101);
        }
        //刀具编号去重判断
        int size = addDzToolGroupVo.getToolNoList().size();
        List<Integer> collect = addDzToolGroupVo.getToolNoList().stream().distinct().collect(Collectors.toList());
        if (collect.size() != size) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_106);
        }
        //添加数据
        DzToolGroups dzToolGroups = new DzToolGroups();
        dzToolGroups.setOrgCode(orgCode);
        dzToolGroups.setGroupNo(addDzToolGroupVo.getGroupNo());
        this.baseMapper.insert(dzToolGroups);
        List<DzToolInfo> toolList = new ArrayList<>();
        for (Integer toolNo : addDzToolGroupVo.getToolNoList()) {
            DzToolInfo dzToolInfo = new DzToolInfo();
            dzToolInfo.setToolGroupsId(dzToolGroups.getToolGroupsId());
            dzToolInfo.setToolNo(toolNo);
            dzToolInfo.setOrgCode(orgCode);
            toolList.add(dzToolInfo);
        }
        infoDao.insertBatchSomeColumn(toolList);
        return Result.ok();
    }


    @Override
    public Result delToolGroups(String toolGroupsId) {
        if (toolGroupsId == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_103);
        }
        DzToolGroups dzToolGroups = this.baseMapper.selectById(toolGroupsId);
        if (dzToolGroups == null) {
            return Result.ok();
        }
        //判断刀具组是否绑定了设备
        QueryWrapper<DzToolCompensationData> wrapper = new QueryWrapper<>();
        wrapper.eq("group_no", dzToolGroups.getGroupNo());
        List<DzToolCompensationData> list = compensationDataDao.selectList(wrapper);
        if (list.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_102);
        }
        //删除刀具组下面的所有刀具
        infoDao.delete(new QueryWrapper<DzToolInfo>().eq("tool_groups_id", toolGroupsId));
        //删除刀具组
        this.baseMapper.deleteById(toolGroupsId);
        return Result.ok();
    }


    @Override
    public Result putToolGroups(PutToolGroupsVo putToolGroupsVo) {

        DzToolGroups dzToolGroups = this.baseMapper.selectById(putToolGroupsVo.getToolGroupsId());
        if (dzToolGroups == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_104);
        }
        if (dzToolGroups.getGroupNo().intValue() != putToolGroupsVo.getGroupNo().intValue()) {
            //刀具组编号有变化，判断新的刀具组编号是否存在
            List<DzToolGroups> groupNo = this.baseMapper.selectList(new QueryWrapper<DzToolGroups>().eq("group_no", putToolGroupsVo.getGroupNo()));
            if (groupNo.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_105);
            } else {
                dzToolGroups.setGroupNo(putToolGroupsVo.getGroupNo());
                this.baseMapper.updateById(dzToolGroups);
            }
        }
        return Result.ok();
    }


    @Override
    public Result getToolInfoList(Long toolGroupsId, PageLimit pageLimit) {
        QueryWrapper<DzToolInfo> tool_groups_id = new QueryWrapper<DzToolInfo>();
        tool_groups_id.eq("tool_groups_id", toolGroupsId);
        if (!StringUtils.isEmpty(pageLimit.getField())) {
            pageLimit.setField(UnderlineTool.humpToLine(pageLimit.getField()));
            if (!StringUtils.isEmpty(pageLimit.getType())) {
                if (pageLimit.getType().equals("ASC")) {
                    tool_groups_id.orderByAsc(pageLimit.getField());
                } else if (pageLimit.getType().equals("DESC")) {
                    tool_groups_id.orderByDesc(pageLimit.getField());
                }
            }
        }
        List<DzToolInfo> dzToolInfos = infoDao.selectList(tool_groups_id);
        return Result.ok(dzToolInfos);
    }


    @Override
    public Result delToolInfo(Long id) {
        DzToolInfo dzToolInfo = infoDao.selectById(id);
        if (dzToolInfo == null) {
            log.error("刀具id不存在，id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        DzToolGroups dzToolGroups = this.baseMapper.selectById(dzToolInfo.getToolGroupsId());
        if (dzToolGroups == null) {
            log.error("刀具组id不存在，id:{}", dzToolInfo.getToolGroupsId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        Long count = compensationDataDao.getToolCompensationDataByToolInfo(dzToolInfo.getToolNo(), dzToolGroups.getGroupNo());
        if (count > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_107);
        } else {
            infoDao.deleteById(id);
            return Result.ok();
        }

    }


    @Override
    public Result putToolInfo(Long id, Integer toolNo) {
        DzToolInfo dzToolInfo = infoDao.selectById(id);
        if (dzToolInfo == null) {
            log.error("刀具id不存在，id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        DzToolGroups dzToolGroups = this.baseMapper.selectById(dzToolInfo.getToolGroupsId());
        if (dzToolGroups == null) {
            log.error("刀具组id不存在，id:{}", dzToolInfo.getToolGroupsId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        if (dzToolInfo.getToolNo().intValue() != toolNo.intValue()) {
            //刀具号发生变化
            //判断新的刀具号是否重复
            QueryWrapper<DzToolInfo> wrapper = new QueryWrapper<DzToolInfo>();
            wrapper.eq("tool_groups_id", dzToolInfo.getToolGroupsId());
            wrapper.eq("tool_no", toolNo);
            List<DzToolInfo> dzToolInfos = infoDao.selectList(wrapper);
            if (dzToolInfos.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_106);
            }
            //修改绑定设备的数据表  刀具编号
            compensationDataDao.updateByToolNo(dzToolInfo.getToolNo(), dzToolGroups.getGroupNo(), toolNo);
            //修改刀具表  刀具编号
            dzToolInfo.setToolNo(toolNo);
            infoDao.updateById(dzToolInfo);
        }
        return Result.ok();
    }


    @Override
    public Result addToolInfo(String toolGroupId, Integer toolNo) {
        DzToolGroups dzToolGroups = this.baseMapper.selectById(toolGroupId);
        if (dzToolGroups == null) {
            log.error("刀具组id不存在，id:{}", toolGroupId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        //判断刀具组下编号是否重复
        QueryWrapper<DzToolInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("tool_groups_id", toolGroupId);
        wrapper.eq("tool_no", toolNo);
        List<DzToolInfo> dzToolInfos = infoDao.selectList(wrapper);
        if (dzToolInfos.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_106);
        }
        DzToolInfo dzToolInfo = new DzToolInfo();
        dzToolInfo.setToolGroupsId(toolGroupId);
        dzToolInfo.setToolNo(toolNo);
        dzToolInfo.setOrgCode(dzToolGroups.getOrgCode());
        infoDao.insert(dzToolInfo);
        return Result.ok(dzToolInfo);
    }
}
