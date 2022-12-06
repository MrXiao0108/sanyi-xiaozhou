package com.dzics.data.appoint.changsha.mom.model.dto.dnc;

import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import lombok.Data;

/**
 * @author: van
 * @since: 2022-06-27
 */
@Data
public class DNCDto {

    @Data
    private static class Base {

        private String task_number;

        private String material_code;

        private String routing_code;

        private String sequenceNumber;

        private String working_procedure;

        private String work_center;

        private String machine_code;

        private String tokenstr;
    }

    @Data
    public static class DownloadProgram extends Base {

        private String programname;

    }

    @Data
    public static class Feedback extends Base {

        private String programname;

        private String dresult;

        private String detail;
    }

    public enum FeedbackEnum {

        /*
         * 成功
         */
        SUCCESS("200"),

        /*
         * 失败
         */
        FAIL("201");

        private final String val;

        FeedbackEnum(String val) {
            this.val = val;
        }

        public String val() {
            return val;
        }
    }

    public static DNCDto.DownloadProgram downloadProgram(DncProgram dncProgram) {
        DownloadProgram model = new DownloadProgram();
        model.setTask_number(dncProgram.getTaskNumber());
        model.setMaterial_code(dncProgram.getMaterialCode());
        model.setRouting_code(dncProgram.getRoutingCode());
        model.setSequenceNumber(dncProgram.getSequencenumber());
        model.setWorking_procedure(dncProgram.getWorkingProcedure());
        model.setWork_center(dncProgram.getWorkCenter());
        model.setMachine_code(dncProgram.getMachineCode());
        model.setTokenstr(dncProgram.getTokenstr());
        model.setProgramname("");
        return model;
    }
}

