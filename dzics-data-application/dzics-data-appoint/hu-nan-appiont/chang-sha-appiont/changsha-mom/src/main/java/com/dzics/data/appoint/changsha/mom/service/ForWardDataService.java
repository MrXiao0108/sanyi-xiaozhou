package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;

/**
 * @author xnb
 * @date 2022/10/8 0008 10:52
 */
public interface ForWardDataService {


    /**
     * 处理岛转发数据
     *
     * @param msg
     * @return ResultDto
     */
    ResultDto forWardData(String msg);
}
