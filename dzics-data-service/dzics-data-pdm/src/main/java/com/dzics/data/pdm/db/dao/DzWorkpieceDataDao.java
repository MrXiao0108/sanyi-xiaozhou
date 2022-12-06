package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pdm.db.model.dao.GetDetectionOneDo;
import com.dzics.data.pdm.db.model.dto.DetectorDataQuery;
import com.dzics.data.pdm.model.dto.CheckItems;
import com.dzics.data.pdm.model.entity.DzWorkpieceData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备检测数据V2新版记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
public interface DzWorkpieceDataDao extends BaseMapper<DzWorkpieceData> {

    List<DzWorkpieceData> getQrCodeOutOk(@Param("qrCode") String qrCode);

    List<DzWorkpieceData> getQrCodeProduct(@Param("qrCode") String qrCode);

    List<CheckItems> getProductIdCheckItems(@Param("productId") String productId, @Param("orderId") String orderId, @Param("lineId") String lineId);

    /**
     * 未绑定二维码的检测数据
     *
     * @return
     */
    List<Map<String, Object>> notBoundQrCode(List<String> idList);


    List<Map<String, String>> getNewestThreeDataId(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("modelNumber") String modelNumber, @Param("size") int size);

    /**
     * 查询检测数据列表
     *
     * @return
     */
    List<Map<String, Object>> selDetectorData(DetectorDataQuery detectorDataQuery);


    List<BigDecimal> getChartsData(@Param("productNo") String productNo, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("fieldName") String fieldName);

    /**
     * 查询最近9条检测记录
     *
     * @param lineNo
     * @param orderNo
     * @param tableColVal
     * @param outOkVal
     * @return
     */
    List<GetDetectionOneDo> selectDataList(@Param("tableColVal") String tableColVal,
                                           @Param("outOkVal") String outOkVal,
                                           @Param("orderNo") String orderNo,
                                           @Param("lineNo") String lineNo);

    List<BigDecimal> getDetectionByMachine(@Param("productNo") String productNo,
                                           @Param("orderNo") String orderNo,
                                           @Param("lineNo") String lineNo,
                                           @Param("fieldName") String fieldName,
                                           @Param("machine") String machine);

    List<DzWorkpieceData> getOneWorkpieceData(@Param("orderNo") String orderNo,
                                              @Param("lineNo") String lineNo,
                                              @Param("productName") String productName,
                                              @Param("producBarcode") String producBarcode,
                                              @Param("newDate") LocalDate newDate,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate,
                                              @Param("number") Integer number);

    DzWorkpieceData getLastDzWorkpieceData(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("now") String now);

    List<Map<String, String>> getNewestThreeDataIdQrcode(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo,
                                                         @Param("modelNumber") String modelNumber, @Param("size") int size,
                                                         @Param("qrCode") boolean qrCode);
}
