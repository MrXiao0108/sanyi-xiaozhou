package com.dzics.data.boardms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.Message;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.boardms.db.dao.SysInterfaceMethodDao;
import com.dzics.data.boardms.db.dao.SysMethodGroupConfigurationDao;
import com.dzics.data.boardms.db.dao.SysMethodGroupDao;
import com.dzics.data.boardms.model.dto.DelGpOrMdVo;
import com.dzics.data.boardms.model.dto.UpGpOrMdVo;
import com.dzics.data.boardms.model.entity.SysInterfaceMethod;
import com.dzics.data.boardms.model.entity.SysMethodGroup;
import com.dzics.data.boardms.model.entity.SysMethodGroupConfiguration;
import com.dzics.data.boardms.model.vo.GroupsMethods;
import com.dzics.data.boardms.service.MethodGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 方法组表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-02
 */
@SuppressWarnings("ALL")
@Service
public class MethodGroupServiceImpl extends ServiceImpl<SysMethodGroupDao, SysMethodGroup> implements MethodGroupService {
    @Autowired
    private SysMethodGroupDao sysMethodGroupDao;
    @Autowired
    private SysMethodGroupConfigurationDao groupConfigurationDao;
    @Autowired
    private SysInterfaceMethodDao methodDao;

    @Override
    public List<GroupsMethods> getGroup() {
        List<GroupsMethods> list = new ArrayList<>();
        List<SysMethodGroup> groups = sysMethodGroupDao.selectList(new QueryWrapper<SysMethodGroup>().select("method_group_id", "group_name", "sort_code").orderByAsc("sort_code"));
        for (int i = 0; i < groups.size(); i++) {
            GroupsMethods groupsMethods = new GroupsMethods();
            groupsMethods.setType("0");
            groupsMethods.setParentId("0");
            groupsMethods.setMenuId(groups.get(i).getMethodGroupId());
            groupsMethods.setMethodGroupId(groups.get(i).getMethodGroupId());
            groupsMethods.setGroupName(groups.get(i).getGroupName());
            groupsMethods.setSortCode(groups.get(i).getSortCode().toString());
            list.add(groupsMethods);
        }
        return list;
    }

    @Override
    @Transactional
    public Result delGroupOrMethod(String sub, DelGpOrMdVo delGpOrMdVo) {
        if (StringUtils.isEmpty(delGpOrMdVo.getType())) {
            return new Result(CustomExceptionType.Parameter_Exception, "Type必传");
        }
        try {
            if ("0".equals(delGpOrMdVo.getType())) {
                if (StringUtils.isEmpty(delGpOrMdVo.getMethodGroupId())) {
                    return new Result(CustomExceptionType.Parameter_Exception, "组ID必传");
                }
                SysMethodGroup sysMethodGroup = sysMethodGroupDao.selectOne(new QueryWrapper<SysMethodGroup>().eq("method_group_id", delGpOrMdVo.getMethodGroupId()));
                if (sysMethodGroup == null) {
                    return new Result(CustomExceptionType.OK_NO_DATA, "不存在此组ID数据");
                }
                List<SysMethodGroupConfiguration> lists = groupConfigurationDao.selectList(new QueryWrapper<SysMethodGroupConfiguration>().eq("group_id", delGpOrMdVo.getMethodGroupId()));
                if (lists.size() > 0) {
                    return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_132);
                } else {
                    sysMethodGroupDao.deleteById(sysMethodGroup.getMethodGroupId());
                    return new Result(CustomExceptionType.OK, Message.OK_2);
                }
            }
            if ("1".equals(delGpOrMdVo.getType())) {
                if (StringUtils.isEmpty(delGpOrMdVo.getMethodGroupId())) {
                    return new Result(CustomExceptionType.Parameter_Exception, "组ID必传");
                }
                if (StringUtils.isEmpty(delGpOrMdVo.getInterfaceId())) {
                    return new Result(CustomExceptionType.Parameter_Exception, "接口ID必传");
                }
                SysMethodGroupConfiguration sysMethodGroupConfiguration = groupConfigurationDao.selectOne(new QueryWrapper<SysMethodGroupConfiguration>()
                        .eq("method_id", delGpOrMdVo.getInterfaceId())
                        .eq("group_id", delGpOrMdVo.getMethodGroupId()));
                //删除关系
                groupConfigurationDao.deleteById(sysMethodGroupConfiguration.getGroupConfigurationId());
                //删除方法
                methodDao.deleteById(delGpOrMdVo.getInterfaceId());
                return new Result(CustomExceptionType.OK, Message.OK_2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("前端路由看板接口组配置，删除操作失败", e);
        }
        return null;
    }

    @Override
    @Transactional
    public Result addGroupOrMethod(String sub, UpGpOrMdVo addGpOrMdVo) {
        Result result = new Result();
        if (addGpOrMdVo.getType().equals("0")) {
            //添加组
            if (StringUtils.isEmpty(addGpOrMdVo.getGroupName())) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "组名称必填");
            }

            List<SysMethodGroup> list = sysMethodGroupDao.selectList(new QueryWrapper<SysMethodGroup>().eq("group_name", addGpOrMdVo.getGroupName()));
            if (!list.isEmpty()) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_133);
            }
            SysMethodGroup newGroup = new SysMethodGroup();
            newGroup.setMethodGroupId(null);
            newGroup.setGroupName(addGpOrMdVo.getGroupName());
            if (StringUtils.isEmpty(addGpOrMdVo.getCacheDuration())) {
                newGroup.setSortCode(new BigDecimal("0"));
            } else {
                newGroup.setSortCode(new BigDecimal(addGpOrMdVo.getCacheDuration()));
            }
            int insert = sysMethodGroupDao.insert(newGroup);
            if (insert == 0) {
                return new Result(CustomExceptionType.SYSTEM_ERROR, Message.ERR_1);
            } else {
                return new Result(CustomExceptionType.OK, Message.OK_1);
            }
        }
        if (addGpOrMdVo.getType().equals("1")) {
            if (StringUtils.isEmpty(addGpOrMdVo.getParentId())) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "组ID必填");
            }
            if (StringUtils.isEmpty(addGpOrMdVo.getBriefIntroduction())) {
                return new Result(CustomExceptionType.Parameter_Exception, "简介必填");
            }
            IdentifierGenerator identifierGenerator = new DefaultIdentifierGenerator();
            //添加方法
            SysInterfaceMethod interfaceMethod = new SysInterfaceMethod();
            interfaceMethod.setInterfaceId(identifierGenerator.nextId(SysInterfaceMethod.class).toString());
            interfaceMethod.setMethodExplain(addGpOrMdVo.getMethodExplain());
            interfaceMethod.setBriefIntroduction(addGpOrMdVo.getBriefIntroduction());
            interfaceMethod.setMethodName(addGpOrMdVo.getMethodName());
            interfaceMethod.setBeanName(addGpOrMdVo.getBeanName());
            interfaceMethod.setSortCode(addGpOrMdVo.getSortCode());
            if (StringUtils.isEmpty(addGpOrMdVo.getCacheDuration())) {
                interfaceMethod.setCacheDuration(0);
            } else {
                interfaceMethod.setCacheDuration(Integer.parseInt(addGpOrMdVo.getCacheDuration().toString()));
            }
            interfaceMethod.setResponseName(addGpOrMdVo.getResponseName());
            int i = methodDao.insert(interfaceMethod);

            //添加关系
            SysMethodGroupConfiguration configuration = new SysMethodGroupConfiguration();
            configuration.setGroupConfigurationId(null);
            configuration.setGroupId(addGpOrMdVo.getParentId());
            configuration.setMethodId(interfaceMethod.getInterfaceId());
            int ii = groupConfigurationDao.insert(configuration);
            if (i > 0 && ii > 0) {
                result = new Result(CustomExceptionType.OK, Message.OK_1);
            }
        }
        return result;
    }

    @Override
    public List<GroupsMethods> getGroupsWithConfig(String sub) {
        List<GroupsMethods> list = new ArrayList<>();
        //获取所有组
        List<SysMethodGroup> groups = sysMethodGroupDao.selectList(new QueryWrapper<SysMethodGroup>());
        if (groups.isEmpty()) {
            return list;
        }
        for (int i = 0; i < groups.size(); i++) {
            GroupsMethods groupsMethods = new GroupsMethods();
            groupsMethods.setMethodGroupId(groups.get(i).getMethodGroupId());
            groupsMethods.setGroupName(groups.get(i).getGroupName());
            groupsMethods.setSortCode(groups.get(i).getSortCode().toString());
            groupsMethods.setParentId("0");
            groupsMethods.setType("0");
            groupsMethods.setChildren(new ArrayList<>());
            groupsMethods.setMenuId(groups.get(i).getMethodGroupId());
            list.add(groupsMethods);
            List<SysMethodGroupConfiguration> configurations = groupConfigurationDao.selectList(new QueryWrapper<SysMethodGroupConfiguration>().eq("group_id", groups.get(i).getMethodGroupId()));
            //取出改组下绑定的所有接口ID
            List<String> collect = configurations.stream().map(gp -> gp.getMethodId()).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                List<SysInterfaceMethod> methods = methodDao.selectList(new QueryWrapper<SysInterfaceMethod>().in("interface_id", collect));
                if (!methods.isEmpty()) {
                    for (int j = 0; j < methods.size(); j++) {
                        GroupsMethods groupsMethods1 = new GroupsMethods();
                        groupsMethods1.setMethodGroupId(groups.get(i).getMethodGroupId());
                        groupsMethods1.setMenuId(methods.get(j).getInterfaceId());
                        groupsMethods1.setParentId(groups.get(i).getMethodGroupId());
                        groupsMethods1.setInterfaceId(methods.get(j).getInterfaceId());
                        groupsMethods1.setMethodExplain(methods.get(j).getMethodExplain());
                        groupsMethods1.setBriefIntroduction(methods.get(j).getBriefIntroduction());
                        groupsMethods1.setMethodName(methods.get(j).getMethodName());
                        groupsMethods1.setSortCode("0");
                        groupsMethods1.setBeanName(methods.get(j).getBeanName());
                        groupsMethods1.setCacheDuration(methods.get(j).getCacheDuration());
                        groupsMethods1.setResponseName(methods.get(j).getResponseName());
                        groupsMethods1.setChildren(new ArrayList<>());
                        groupsMethods1.setSortCode(String.valueOf(methods.get(j).getSortCode()));
                        groupsMethods1.setType("1");
                        list.add(groupsMethods1);
                    }
                }
            }
        }
        Collections.sort(list, new Comparator<GroupsMethods>() {
            @Override
            public int compare(GroupsMethods o1, GroupsMethods o2) {
                return new BigDecimal(o1.getSortCode()).compareTo(new BigDecimal(o2.getSortCode()));
            }
        });
        return list;
    }

    @Override
    @Transactional
    public Result upInterfaceByGroup(String sub, UpGpOrMdVo upGpOrMdVo) {
        if (StringUtils.isEmpty(upGpOrMdVo.getType())) {
            return new Result(CustomExceptionType.Parameter_Exception, "类型必传");
        }

        //修改组
        if ("0".equals(upGpOrMdVo.getType())) {
            if (StringUtils.isEmpty(upGpOrMdVo.getParentId())) {
                return new Result(CustomExceptionType.Parameter_Exception, "组ID必传");
            }
            if (StringUtils.isEmpty(upGpOrMdVo.getGroupName())) {
                return new Result(CustomExceptionType.Parameter_Exception, "组名称必填");
            }
            SysMethodGroup sysMethodGroup = sysMethodGroupDao.selectById(upGpOrMdVo.getParentId());
            if (sysMethodGroup == null) {
                return new Result(CustomExceptionType.OK_NO_DATA, "请检查是否存在当前组ID");
            }
            sysMethodGroup.setMethodGroupId(upGpOrMdVo.getParentId());
            sysMethodGroup.setGroupName(upGpOrMdVo.getGroupName());
            sysMethodGroup.setSortCode(upGpOrMdVo.getSortCode());
            int i = sysMethodGroupDao.updateById(sysMethodGroup);
        }

        //修改明细
        if ("1".equals(upGpOrMdVo.getType())) {
            if (StringUtils.isEmpty(upGpOrMdVo.getParentId())) {
                return new Result(CustomExceptionType.Parameter_Exception, "组ID必传");
            }
            if (StringUtils.isEmpty(upGpOrMdVo.getInterfaceId())) {
                return new Result(CustomExceptionType.Parameter_Exception, "接口ID必传");
            }
            SysInterfaceMethod interfaceMethod = methodDao.selectById(upGpOrMdVo.getInterfaceId());
            //修改方法表
            SysInterfaceMethod newInterFace = new SysInterfaceMethod();
            newInterFace.setInterfaceId(interfaceMethod.getInterfaceId());
            newInterFace.setMethodExplain(upGpOrMdVo.getMethodExplain());
            newInterFace.setBriefIntroduction(upGpOrMdVo.getBriefIntroduction());
            newInterFace.setMethodName(upGpOrMdVo.getMethodName());
            newInterFace.setBeanName(upGpOrMdVo.getBeanName());
            newInterFace.setCacheDuration(Integer.valueOf(upGpOrMdVo.getCacheDuration()));
            newInterFace.setResponseName(upGpOrMdVo.getResponseName());
            newInterFace.setSortCode(upGpOrMdVo.getSortCode());
            int i1 = methodDao.updateById(newInterFace);
            //取出关系表
            SysMethodGroupConfiguration oldConfig = groupConfigurationDao.selectOne(new QueryWrapper<SysMethodGroupConfiguration>()
                    .eq("group_id", upGpOrMdVo.getParentId())
                    .eq("method_id", upGpOrMdVo.getInterfaceId()));
            SysMethodGroupConfiguration newConfig = new SysMethodGroupConfiguration();
            newConfig.setGroupConfigurationId(null);
            newConfig.setGroupId(upGpOrMdVo.getParentId());
            newConfig.setMethodId(upGpOrMdVo.getInterfaceId());
            int i = groupConfigurationDao.deleteById(oldConfig.getGroupConfigurationId());
            int insert = groupConfigurationDao.insert(newConfig);
        }
        return new Result(CustomExceptionType.OK, Message.OK_3);
    }

}
