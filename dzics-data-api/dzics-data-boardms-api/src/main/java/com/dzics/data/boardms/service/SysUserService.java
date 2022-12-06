package com.dzics.data.boardms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.boardms.model.dao.UserListRes;
import com.dzics.data.boardms.model.entity.SysUser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 根据账号获取用户信息
     *
     * @param sub
     * @return
     */
    @Cacheable(cacheNames = "sysUserService.getByUserName", key = "#a0")
    SysUser getByUserName(String sub);

}
