package com.dzics.data.business;

/**
 * @Classname Test2
 * @Description 描述
 * @Date 2022/3/16 11:15
 * @Created by NeverEnd
 */
public class Test2 {
    public static void main(String[] args) {
        String order_code = "DZ-2027";
        int i = Math.abs(order_code.hashCode() % 2) + 2;
        System.out.println(i);
        String line_no = "1";
        String product_no = "1030";
        String sh = order_code + line_no + product_no;
        int sha = Math.abs(sh.hashCode() % 20) + 1;
        System.out.println(sha);
    }
}
