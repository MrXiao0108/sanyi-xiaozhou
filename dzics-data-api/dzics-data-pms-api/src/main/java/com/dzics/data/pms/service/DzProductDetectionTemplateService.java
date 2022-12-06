package com.dzics.data.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.dto.DzProductDetectionTemplateParms;
import com.dzics.data.pms.model.dto.EditProDuctTemp;
import com.dzics.data.pms.model.dto.TableColumnDto;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.DBDetectTempVo;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
public interface DzProductDetectionTemplateService extends IService<DzProductDetectionTemplate> {

    /**
     * 根据产品Id 获取产品检测项配置
     *
     *
     * @param orderId
     * @param lineId
     * @param productNo
     * @param sub
     * @return
     */
    Result selProductTemplate(String orderId, String lineId, String productNo, String sub);

    /**
     * 根据产品id(序号)查询 检测项列表
     * @param productId
     * @return
     */
    Result getByProductId(String productId);

    List<DzProductDetectionTemplateParms> listGroupBy(String field, String type, String productName, String departId, String orderId, String lineId);


    List<TableColumnDto> listProductNo(String productNo, String orderNo, String lineNo);

    /**
     * 根据产品Id 获取检测配置模板
     *
     * @param orderId
     * @param lineId
     * @param productNo
     * @return
     */
    List<DzDetectTempVo> selProductTemplateProductId(String orderId, String lineId, String productNo);

    /**
     * 根据产品编号 和 检测 检测项获取 上线下线值
     * @param productNo
     * @param item
     * @return
     */
    DzProductDetectionTemplate getProductNoItem(String productNo, String item);

    List<Map<String, Object>> listProductId(String productNo);

    List<TableColumnDto> getDefoutDetectionTemp();

    /**
     * 根据产品站点获取 ，是否存在配置
     * @param productNo 产品编号
     * @param departId 站点id
     * @param orderId
     * @param lineId
     * @return
     */
    Integer getProductNo(String productNo, Long departId, String orderId, String lineId);

    /**
     * @param addtempLs
     * 保存产品检测项设置
     * @return
     */
    boolean save(List<DzProductDetectionTemplate> addtempLs);

    /**
     * 修改检测配置
     *
     * @param templateParm
     * @return
     */
    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    Result editProDetectorItem(EditProDuctTemp templateParm);

    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    boolean delGroupupId(String groupId);

    /**
     * @param groupId 获取对比检测值
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getEditDBDetectorItem", key = "#groupId")
    List<DBDetectTempVo> getEditDBDetectorItem(String groupId);

    /**
     * @param groupId 分组id信息
     * @return 配置信息
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getEditDetectorItem", key = "#groupId")
    List<DzDetectTempVo> getEditDetectorItem(String groupId);

    @Cacheable(cacheNames = "dzDetectionTemplCache.getByOrderNoProNo", key = "#departId+#productNo+#orderId1+#lineId1")
    List<DzProductDetectionTemplate> getByOrderNoProNo(Long departId, String productNo, String orderId1, String lineId1);
    /**
     * 对比值修改
     *
     * @param editProDuctTemp
     * @return
     */
    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    Result dbProDetectorItem(EditProDuctTemp editProDuctTemp);

}
