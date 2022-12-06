package com.dzics.data.maintain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.BaseTimeLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.MaintainDetailsParms;
import com.dzics.data.maintain.model.dto.MaintainDeviceParms;
import com.dzics.data.maintain.model.dto.MaintainRecordParms;
import com.dzics.data.maintain.model.entity.DzMaintainDevice;
import com.dzics.data.maintain.model.vo.MaintainDevice;
import com.dzics.data.maintain.model.vo.MaintainRecord;
import com.dzics.data.maintain.model.vo.MaintainRecordDetails;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 保养设备配置 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzMaintainDeviceService extends IService<DzMaintainDevice> {


    List<MaintainDevice> getMaintainListWait(String lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now);


    List<MaintainDevice> getMaintainListOver(String lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now);

    List<MaintainRecord> getMaintainRecord(BaseTimeLimit pageLimit, MaintainRecordParms parmsReq);

    List<MaintainRecordDetails> getMaintainRecordDetails(String maintainHistoryId);

    Result getMaintainRecordDetails(String sub, MaintainDetailsParms parmsReq);

    Result<List<MaintainDevice>> getMaintainList(String sub, BaseTimeLimit pageLimit, MaintainDeviceParms parmsReq, String useOrgCode);

    Result getMaintainRecord(String sub, BaseTimeLimit pageLimit, MaintainRecordParms parmsReq);

    Result delMaintainDevice(String sub, String maintainId);

    DzMaintainDevice getByDeviceId(String deviceId);
}
