package com.dzics.data.pub.service;


import com.dzics.data.common.base.dto.KbParms;
import com.dzics.data.common.base.vo.Result;
import org.springframework.stereotype.Component;

/**
 * 接口组合
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
public interface InterfaceCombination {

    /**
     * 组合调用传递的所有接口 方法 集合 封装返回
     *
     * @param kbParms 接口方法集合
     * @return
     */
    Result getInterFaceMethods(KbParms kbParms);
}
