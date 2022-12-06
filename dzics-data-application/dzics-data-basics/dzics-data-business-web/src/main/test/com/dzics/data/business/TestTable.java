package com.dzics.data.business;

/**
 * @Classname TestTable
 * @Description 描述
 * @Date 2022/3/1 9:01
 * @Created by NeverEnd
 */
public class TestTable {
    public static void main(String[] args) {
        String al = "";
        for (int i = 1; i < 21; i++) {
            String x = "CREATE TABLE `dz_working_flow_"+i+"` (\n" +
                    "  `process_flow_id` varchar(20) NOT NULL COMMENT '工序流程详情ID',\n" +
                    "  `station_id` varchar(20) DEFAULT NULL COMMENT '工位ID',\n" +
                    "  `qr_code` varchar(100) DEFAULT NULL COMMENT '工件二维码',\n" +
                    "  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',\n" +
                    "  `line_id` bigint(20) DEFAULT NULL COMMENT '产线ID',\n" +
                    "  `working_procedure_id` varchar(20) DEFAULT NULL COMMENT '工序ID',\n" +
                    "  `pro_task_id` varchar(20) DEFAULT NULL COMMENT '生产任务订单Id',\n" +
                    "  `workpiece_code` varchar(200) DEFAULT NULL COMMENT '工件编码',\n" +
                    "  `start_time` datetime(3) DEFAULT NULL COMMENT '开始时间',\n" +
                    "  `start_ropert_time` datetime DEFAULT NULL COMMENT '生产开始上报时间',\n" +
                    "  `start_reporting_status` varchar(2) DEFAULT NULL COMMENT '生产开始 0未上报 ,1 已上报 ,3上报异常',\n" +
                    "  `start_reporting_frequency` int(11) DEFAULT '0' COMMENT '生产开始 上报次数',\n" +
                    "  `complete_time` datetime(3) DEFAULT NULL COMMENT '完成时间',\n" +
                    "  `complete_ropert_time` datetime DEFAULT NULL COMMENT '生产完成上报时间',\n" +
                    "  `complete_reporting_status` varchar(2) DEFAULT NULL COMMENT '生产完成  0未上报 ,1 已上报 ,3上报异常',\n" +
                    "  `complete_reporting_frequency` int(11) DEFAULT '0' COMMENT '生产完成 上报次数',\n" +
                    "  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',\n" +
                    "  `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',\n" +
                    "  `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',\n" +
                    "  `work_date` date DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`process_flow_id`),\n" +
                    "  KEY `qr_code` (`qr_code`,`line_id`,`order_id`,`create_time`) USING BTREE,\n" +
                    "  KEY `station_id` (`station_id`,`pro_task_id`,`line_id`,`order_id`,`qr_code`,`workpiece_code`,`work_date`) USING BTREE,\n" +
                    "  KEY `line_id` (`line_id`) USING BTREE\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工件制作流程记录';";
            al = al+x;
        }
        System.out.println(al);
    }
}
