package com.dzics.data.appoint.changsha.mom.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author xnb
 * @date 2022/11/8 0008 11:30
 */
@Component
@Slf4j
public class ListUtil {

    public Integer getFieldNum(List<String>list,String field){
        Integer num = 0;
        if(CollectionUtils.isEmpty(list)){
            return num;
        }
        for (String s : list) {
            if(field.equals(s)){
                num++;
            }
        }
        return num;
    }

}
