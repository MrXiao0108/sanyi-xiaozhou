package com.dzics.data.appoint.changsha.mom.enums;

/**
 * @author: van
 * @since: 2022-08-19
 */
public class MomEnum {

    public enum CommunicationLog {

        /*
         * 叫料
         */
        JIAO_LIAO("101"),

        /*
         * 叫空框
         */
        JIAO_KONG_KUANG("102"),

        /*
         * 满料拖出
         */
        MAN_LIAO_TUO_CHU("103"),

        /*
         * 空框拖出
         */
        KONG_KUANG_TUO_CHU("104"),

        /*
         * 报工
         */
        BAO_GONG("105"),

        /*
         * 查询料框信息
         */
        CHA_XUN_LIAO_KUANG("106");

        private final String val;

        CommunicationLog(String val) {
            this.val = val;
        }

        public String val() {
            return val;
        }
    }

    public enum CommunicationLogType {

        /*
         * mom下发
         */
        IN("1"),

        /*
         * 请求mom
         */
        OUT("2");

        private final String val;

        CommunicationLogType(String val) {
            this.val = val;
        }

        public String val() {
            return val;
        }
    }
}
