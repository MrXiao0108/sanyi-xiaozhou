package com.dzics.data.pub.service.kanban;


import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.pub.model.dao.SelectTrendChartDo;


import java.util.List;


/**
 * 产品检测接口
 *
 * @author ZhangChengJun
 * Date 2021/6/3.
 * @since
 */
public interface AccqDzProductService {
    /**
     * 三一产品检测趋势
     * @return
     * @param orderNo
     * @param lineNo
     */


    List<SelectTrendChartDo> getDetectionByMachine(String orderNo, String lineNo, GetOrderNoLineNo data);


}
