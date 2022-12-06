package com.dzics.data.appoint.changsha.mom.enums;

/**
 * @author xnb
 * @date 2022/9/22 0022 8:22
 */
public class OrderSysCode {


    public enum OrderSysCodeType{

        XZCJG01("DZ-1972"),

        XZCJG02("DZ-1973"),

        XZJJG01("DZ-1974"),

        XZJJG02("DZ-1975");


        private final String val;

        OrderSysCodeType(String val) {
            this.val = val;
        }

        public String val() {
            return val;
        }

    }
}
