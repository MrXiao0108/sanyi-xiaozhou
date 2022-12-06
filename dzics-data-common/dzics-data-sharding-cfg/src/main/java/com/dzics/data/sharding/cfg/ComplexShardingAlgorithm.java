package com.dzics.data.sharding.cfg;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Classname ComplexShardingAlgorithm
 * @Description 设备日志表复合分片算法实现
 * @Date 2022/3/1 11:31
 * @Created by NeverEnd
 */
@Slf4j
public class ComplexShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {

    /**
     * Sharding.
     *
     * @param availableTargetNames available data sources or tables's names
     * @param shardingValue        sharding value
     * @return sharding results for data sources or tables's names
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {
        List<String> strings = new ArrayList<>();
        Map<String, Collection<String>> collectionMap = shardingValue.getColumnNameAndShardingValuesMap();
        if (MapUtils.isNotEmpty(collectionMap)) {
            String order_code = (String) collectionMap.get("order_code").toArray()[0];
//            String line_no = (String) collectionMap.get("line_no").toArray()[0];
//            String device_type = (String) collectionMap.get("device_type").toArray()[0];
//            String device_code = (String) collectionMap.get("device_code").toArray()[0];
//            String sh = order_code + device_type + device_code;
            String sh = order_code;
//            String sh = order_code + line_no + device_type + device_code;
            int sha = Math.abs(sh.hashCode() % 20)+1;
            strings.add("sys_real_time_logs_" + sha);
        }
        return strings;
    }
}
