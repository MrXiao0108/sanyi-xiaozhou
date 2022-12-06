package com.dzics.data.boardms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.boardms.model.dao.UserListRes;
import com.dzics.data.boardms.model.entity.SysUser;
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

    List<UserListRes> listUserOrgCode(@Param("useOrgCode") String useOrgCode, @Param("realname") String realname,
                                      @Param("username") String username, @Param("status") Integer status,
                                      @Param("createTime") Date createTime, @Param("endTime") Date endTime);

    Long listUsername(@Param("username") String username);
}
