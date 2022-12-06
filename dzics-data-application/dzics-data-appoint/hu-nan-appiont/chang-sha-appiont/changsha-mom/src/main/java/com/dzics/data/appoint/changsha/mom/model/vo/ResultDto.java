package com.dzics.data.appoint.changsha.mom.model.vo;

import com.dzics.data.appoint.changsha.mom.exception.CustomMomException;
import com.dzics.data.appoint.changsha.mom.model.constant.ResponseStatusMessage;
import com.dzics.data.common.base.vo.Result;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResultDto implements Serializable {
    //版本   必填
    private int version;
    //任务ID  随机生成32位UUID，单次下发指令唯一标识   必填
    private String taskId;
    //返回结果   0：正确；其它：错误   必填
    private String code;
    //返回消息
    private String msg;
    //返回结果集
    private String returnData;

    public static ResultDto error() {
        ResultDto resultDto = new ResultDto();
        resultDto.setCode("1");
        resultDto.setMsg(ResponseStatusMessage.BUST_SERVICE);
        return resultDto;
    }

    public static ResultDto error(CustomMomException e) {
        ResultDto resultDto = new ResultDto();
        resultDto.setCode("1");
        resultDto.setTaskId(e.getTaskId());
        resultDto.setVersion(e.getVersion());
        resultDto.setMsg(e.getMessage());
        return resultDto;
    }

    public static ResultDto ok(int version, String taskId) {
        ResultDto resultDto = new ResultDto();
        resultDto.setVersion(version);
        resultDto.setTaskId(taskId);
        resultDto.setCode("0");
        resultDto.setMsg("");
        resultDto.setReturnData("");
        return resultDto;
    }
}
