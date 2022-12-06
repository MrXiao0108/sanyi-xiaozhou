package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.model.vo.user.UserListRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {

    List<UserListRes> listUserOrgCode(@Param("field") String field, @Param("type") String type,
                                      @Param("useOrgCode") String useOrgCode, @Param("realname") String realname,
                                      @Param("username") String username, @Param("status") Integer status,
                                      @Param("createTime") Date createTime, @Param("endTime") Date endTime);

    Long listUsername(@Param("username") String username);

    Integer getCountByOrgCode(@Param("orgCode") String orgCode);


}
