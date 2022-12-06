package com.dzics.data.pub.service.workreporting;

/**
 * @Classname WorkReportingService
 * @Description 向第三方报工接口定义
 * @Date 2022/6/16 17:11
 * @Created by NeverEnd
 */
public interface WorkReportingService<R, T> {
    /**
     * 向第三方报工接口
     *
     * @param parms               报工数据
     * @param momWaitWorkReportId 待报工记录主键
     * @return 报工结果
     */
    R sendWorkReportingData(T parms, String momWaitWorkReportId);
}
