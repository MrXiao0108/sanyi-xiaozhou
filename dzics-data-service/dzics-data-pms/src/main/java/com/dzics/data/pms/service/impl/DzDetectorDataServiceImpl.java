package com.dzics.data.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzDetectorDataDao;
import com.dzics.data.pms.model.entity.DzDetectorData;
import com.dzics.data.pms.service.DzDetectionTemplCache;
import com.dzics.data.pms.service.DzDetectorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@SuppressWarnings("ALL")
@Service
public class DzDetectorDataServiceImpl extends ServiceImpl<DzDetectorDataDao, DzDetectorData> implements DzDetectorDataService {

    @Autowired
    private DzDetectorDataDao dzDetectorDataMapper;
    @Autowired
    private DzDetectionTemplCache dzDetectionTemplCache;

    /**
     * 分组后的每次检测数据 key
     *
     * @param productNo       产品id
     * @param detectionResult
     * @param startTime
     * @param endTime
     * @param orgCode
     * @return
     */
    @Override
    public List<DzDetectorData> groupBuby(String productNo, Integer detectionResult, Date startTime, Date endTime, String orgCode) {
        List<DzDetectorData> templates = dzDetectorDataMapper.groupBuby(productNo, detectionResult, startTime, endTime, orgCode);
        return templates;
    }

    @Override
    public List<DzDetectorData> getByOrderNoProNo(String groupKey) {
        return null;
    }

    /**
     * 分组后的每次检测数据 key
     *
     * @return
     */
    @Override
    public List<DzDetectorData> groupBubyData() {
        List<DzDetectorData> templates = dzDetectorDataMapper.groupBubyData();
        return templates;
    }

    @Override
    public List<Map<String, Object>> getGroupKey(List<String> groupKey) {
        return dzDetectorDataMapper.getGroupKey(groupKey);
    }

    @Override
    public Result delProDetectorItem(Long groupId, String sub) {
        boolean bb = dzDetectionTemplCache.delGroupupId(groupId);
        if (bb) {
            return new Result(CustomExceptionType.OK);
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0);
    }
}
