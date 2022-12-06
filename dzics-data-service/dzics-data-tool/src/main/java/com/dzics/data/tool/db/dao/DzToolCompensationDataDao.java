package com.dzics.data.tool.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.tool.db.model.dao.GetToolInfoDataListDo;
import com.dzics.data.tool.db.model.vo.GetToolInfoDataListVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 刀具补偿数据表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
@Repository
public interface DzToolCompensationDataDao extends BaseMapper<DzToolCompensationData> {


    Long getToolCompensationDataByToolInfo(@Param("toolNo") Integer toolNo, @Param("groupNo") Integer groupNo);

    void updateByToolNo(@Param("toolNo") Integer toolNo,
                        @Param("groupNo") Integer groupNo,
                        @Param("newToolNo") Integer newToolNo);

    List<DzToolCompensationData> getToolConfigureList(@Param("field") String field, @Param("type") String type, @Param("orgCode") String orgCode,
                                                      @Param("groupNo") Integer groupNo);

    /**
     * 查询刀具信息数据
     *
     * @param getToolInfoDataListVo
     * @return
     */
    List<GetToolInfoDataListDo> getToolInfoDataList(GetToolInfoDataListVo getToolInfoDataListVo);

    /**
     * 查询所有绑定刀具的设备
     *
     * @return
     */
    List<Long> getEqIds();

}
