package com.dzics.data.boardms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.boardms.db.dao.*;
import com.dzics.data.boardms.model.dto.*;
import com.dzics.data.boardms.model.entity.*;
import com.dzics.data.boardms.service.InterfaceMethod;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Service
@Slf4j
public class InterfaceMethodImpl extends ServiceImpl<SysInterfaceMethodDao, SysInterfaceMethod> implements InterfaceMethod {

    @Autowired
    private SysInterfaceGroupDao interfaceGroupMapper;
    @Autowired
    private SysInterfaceGroupConfigurationDao inGrConfigMapper;
    @Autowired
    private SysMethodGroupConfigurationDao inMeGrConfigMapper;
    @Autowired
    private SysMethodGroupDao methodGroupMapper;
    @Autowired
    private SysInterfaceMethodDao interfaceMethodMapper;

    /**
     * 获取方法接口方法列表
     *
     * @param sub
     * @param pageLimit
     * @return
     */
    @Override
    public Result getInterfaceMethod(String sub, PageLimit pageLimit) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<SysInterfaceMethod> list = list();
        PageInfo<SysInterfaceMethod> info = new PageInfo<>(list);
        Result ok = Result.ok();
        ok.setData(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }

    /**
     * 新增接口
     *
     * @param sub
     * @param dto
     * @return
     */
    @Override
    public Result addInterfaceMethod(String sub, InterfaceMethodParm dto) {
        SysInterfaceMethod interfaceMethod = new SysInterfaceMethod();
        BeanCopier copier = BeanCopier.create(InterfaceMethodParm.class, SysInterfaceMethod.class, false);
        copier.copy(dto, interfaceMethod, null);
        QueryWrapper<SysInterfaceMethod> wp = new QueryWrapper<>();
        wp.eq("method_name", interfaceMethod.getMethodName());
        wp.eq("bean_name", interfaceMethod.getBeanName());
        wp.or();
        wp.eq("response_name", interfaceMethod.getResponseName());
        List<SysInterfaceMethod> list = list(wp);
        if (CollectionUtils.isEmpty(list)) {
            save(interfaceMethod);
            return Result.ok();
        } else {
            Result result = new Result();
            result.setCode(CustomExceptionType.TOKEN_PERRMITRE_ERROR.getCode());
            result.setMsg(CustomResponseCode.ERR36.getChinese());
            return result;
        }
    }

    /**
     * 编辑接口信息
     *
     * @param sub
     * @param dto
     * @return
     */
    @Override
    public Result editInterfaceMethod(String sub, InterfaceMethodParm dto) {
        SysInterfaceMethod interfaceMethod = new SysInterfaceMethod();
        BeanCopier copier = BeanCopier.create(InterfaceMethodParm.class, SysInterfaceMethod.class, false);
        copier.copy(dto, interfaceMethod, null);
        boolean updateById = updateById(interfaceMethod);
        return Result.ok();
    }

    /**
     * 组列表
     *
     * @param sub
     * @param pageLimit
     * @return
     */
    @Override
    public Result getGroup(String sub, PageLimit pageLimit) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<SysInterfaceGroup> wp = new QueryWrapper<>();
        wp.orderByAsc("sort_code", "group_id");
        List<SysInterfaceGroup> list = interfaceGroupMapper.selectList(wp);
        PageInfo<SysInterfaceGroup> info = new PageInfo<>(list);
        Result<Object> ok = Result.ok();
        ok.setData(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }

    /**
     * 新增组
     *
     * @param sub
     * @param dto
     * @return
     */
    @Override
    public Result addGroup(String sub, InterfaceGroupParm dto) {
        SysInterfaceGroup interfaceGroup = new SysInterfaceGroup();
        BeanCopier copier = BeanCopier.create(InterfaceGroupParm.class, SysInterfaceGroup.class, false);
        copier.copy(dto, interfaceGroup, null);

        QueryWrapper<SysInterfaceGroup> wp = new QueryWrapper<>();
        wp.eq("group_code", interfaceGroup.getGroupCode());
        List<SysInterfaceGroup> list = interfaceGroupMapper.selectList(wp);
        if (CollectionUtils.isEmpty(list)) {
            int save = interfaceGroupMapper.insert(interfaceGroup);
            return Result.ok();
        } else {
            Result result = new Result();
            result.setCode(CustomExceptionType.TOKEN_PERRMITRE_ERROR.getCode());
            result.setMsg(CustomResponseCode.ERR37.getChinese());
            return result;
        }
    }

    /**
     * 编辑组
     *
     * @param sub
     * @param dto
     * @return
     */
    @Override
    public Result editGroup(String sub, InterfaceGroupParm dto) {
        SysInterfaceGroup interfaceGroup = new SysInterfaceGroup();
        BeanCopier copier = BeanCopier.create(InterfaceGroupParm.class, SysInterfaceGroup.class, false);
        copier.copy(dto, interfaceGroup, null);
        int updateById = interfaceGroupMapper.updateById(interfaceGroup);
        return Result.ok();
    }

    /**
     * 设置接口组
     *
     * @param sub
     * @param groupConfiguration
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addGroupInerfaceConfig(String sub, InGrConfiguration groupConfiguration) {
        List<ReqGroupConfiguration> interfaceIdsHandel = new ArrayList<>();
        List<ReqGroupConfiguration> interfaceIds = groupConfiguration.getInterfaceIds();
        for (ReqGroupConfiguration interfaceId : interfaceIds) {
            if (StringUtils.isEmpty(interfaceId.getGroupName())) {
                interfaceIdsHandel.add(interfaceId);
            }
        }
        String groupId = groupConfiguration.getGroupId();
        QueryWrapper<SysInterfaceGroupConfiguration> wp = new QueryWrapper<>();
        wp.eq("group_id", groupId);
        int remove = inGrConfigMapper.delete(wp);
        List<SysInterfaceGroupConfiguration> collect = interfaceIdsHandel.stream().map(inface -> new SysInterfaceGroupConfiguration(groupId, inface.getInterfaceId(), inface.getCacheDuration())).collect(Collectors.toList());
        for (SysInterfaceGroupConfiguration configuration : collect) {
            int b = inGrConfigMapper.insert(configuration);
        }
        return Result.ok();
    }

    /**
     * 查询组配置接口
     *
     * @param sub
     * @param config
     * @return
     */
    @Override
    public Result getGroupInerfaceConfig(String sub, GroupConfigParm config) {
        String groupId = config.getGroupId();
//        组信息
        QueryWrapper<SysInterfaceGroup> wpGroup = new QueryWrapper<>();
        SysInterfaceGroup byId = interfaceGroupMapper.selectById(groupId);
        if (byId == null) {
            return Result.ok(CustomExceptionType.OK_NO_DATA);
        }
//       全部接口信息
        List<SysInterfaceMethod> interfaceMethods = list();
//        已选中接口信息
        QueryWrapper<SysInterfaceGroupConfiguration> wp = new QueryWrapper<>();
        wp.eq("group_id", groupId);
        wp.select("interface_id", "cache_duration");
        List<SysInterfaceGroupConfiguration> list = inGrConfigMapper.selectList(wp);
        ReqGroupConfig reqGroupConfig = new ReqGroupConfig();
        reqGroupConfig.setInterfaceGroup(byId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysInterfaceGroupConfiguration configuration : list) {
                for (SysInterfaceMethod interfaceMethod : interfaceMethods) {
                    if (configuration.getInterfaceId().equals(interfaceMethod.getInterfaceId())) {
                        interfaceMethod.setCacheDuration(configuration.getCacheDuration());
                        interfaceMethod.setIsShow(0);
                        break;
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(interfaceMethods)) {
            List<SysMethodGroupConfiguration> configurationList = inMeGrConfigMapper.selectList(Wrappers.emptyWrapper());
            for (SysInterfaceMethod interfaceMethod : interfaceMethods) {
                String interfaceId = interfaceMethod.getInterfaceId();
                for (SysMethodGroupConfiguration sysMethodGroupConfiguration : configurationList) {
                    String methodId = sysMethodGroupConfiguration.getMethodId();
                    if (interfaceId.equals(methodId)) {
                        interfaceMethod.setParentId(sysMethodGroupConfiguration.getGroupId());
                        break;
                    }
                }
            }
            List<SysMethodGroup> methodGroups = methodGroupMapper.selectList(Wrappers.emptyWrapper());
            for (SysMethodGroup methodGroup : methodGroups) {
                SysInterfaceMethod interfaceMethod = new SysInterfaceMethod();
                interfaceMethod.setParentId("0");
                interfaceMethod.setInterfaceId(methodGroup.getMethodGroupId());
                interfaceMethod.setGroupName(methodGroup.getGroupName());
                interfaceMethod.setSortCode(methodGroup.getSortCode());
                interfaceMethods.add(interfaceMethod);
            }
        }
        reqGroupConfig.setInterfaceMethods(interfaceMethods);
        return Result.ok(reqGroupConfig);
    }

    /**
     * 根据组名称获取接口列表
     *
     * @param methodGroup 接口组唯一编码
     * @return
     */
    @Override
    public Result getMethodGroup(String methodGroup) {
        Result ok = Result.ok();
        QueryWrapper<SysInterfaceGroup> wp = new QueryWrapper<>();
        wp.eq("group_code", methodGroup);
        SysInterfaceGroup interfaceGroup = interfaceGroupMapper.selectOne(wp);
        if (interfaceGroup != null) {
            String groupId = interfaceGroup.getGroupId();
            QueryWrapper<SysInterfaceGroupConfiguration> wpConf = new QueryWrapper<>();
            wpConf.select("interface_id", "cache_duration");
            wpConf.eq("group_id", groupId);
            List<SysInterfaceGroupConfiguration> list = inGrConfigMapper.selectList(wpConf);
            if (CollectionUtils.isNotEmpty(list)) {
                List<String> interfaceId = list.stream().map(conf -> conf.getInterfaceId()).collect(Collectors.toList());
                QueryWrapper<SysInterfaceMethod> wpInterface = new QueryWrapper<>();
                wpInterface.in("interface_id", interfaceId);
                List<SysInterfaceMethod> sysInterfaceMethods = interfaceMethodMapper.selectList(wpInterface);
                for (SysInterfaceMethod interfaceMethod : sysInterfaceMethods) {
                    for (SysInterfaceGroupConfiguration configuration : list) {
                        if (interfaceMethod.getInterfaceId().equals(configuration.getInterfaceId())) {
                            interfaceMethod.setCacheDuration(configuration.getCacheDuration());
                        }
                    }
                }
                ok.setData(sysInterfaceMethods);
                return ok;
            }
        }
        return ok;
    }

    /**
     * 根据接口id删除
     *
     * @param sub
     * @param delInterfaceMethod
     * @return
     */
    @Override
    public Result delInterfaceMethod(String sub, DelInterfaceMethod delInterfaceMethod) {
        int b = interfaceMethodMapper.deleteById(delInterfaceMethod.getInterfaceId());
        return Result.ok();
    }

    /**
     * 删除组接口
     *
     * @param sub
     * @param interfaceGroup
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delGroup(String sub, DelInterfaceGroup interfaceGroup) {
        String groupId = interfaceGroup.getGroupId();
        int b = interfaceGroupMapper.deleteById(groupId);
        QueryWrapper<SysInterfaceGroupConfiguration> wp = new QueryWrapper<>();
        wp.eq("group_id", groupId);
        int remove = inGrConfigMapper.delete(wp);
        return Result.ok();
    }
}
