package com.dzics.data.kanban.changsha.changpaoguang.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.dto.InterFaceMethodParms;
import com.dzics.data.common.base.dto.KbParms;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.config.SpringContextUtil;
import com.dzics.data.pub.service.InterfaceCombination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口组合
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Service
@Slf4j
public class InterfaceCombinationImpl implements InterfaceCombination {


    /**
     * 组合调用传递的所有接口 方法 集合 封装返回
     *
     * @param kbParms 接口方法集合
     * @return
     */
    @Override
    public Result getInterFaceMethods(KbParms kbParms) {
        List<InterFaceMethodParms> interfaceListMethod = kbParms.getInterfaceMethods();
        GetOrderNoLineNo orderNoLineNo = kbParms.getOrderNoLineNo();
        Result<Object> ok = Result.ok();
        if (CollectionUtils.isNotEmpty(interfaceListMethod)) {
            Map<String, Object> map = new HashMap<>();
            for (InterFaceMethodParms method : interfaceListMethod) {
                try {
                    String beanName = method.getBeanName();
                    Object bean = SpringContextUtil.getBean(beanName);
                    Class beanClass = bean.getClass();
                    Integer cacheDuration = method.getCacheDuration();
                    orderNoLineNo.setCacheTime(cacheDuration);
                    Method m = beanClass.getDeclaredMethod(method.getMethodName(), GetOrderNoLineNo.class);
                    Object invoke = m.invoke(bean, orderNoLineNo);
                    map.put(method.getResponseName(), invoke);
                } catch (NoSuchMethodException e) {
                    log.error("获取Bean中的方法失败：{}", e.getMessage(),e);
                } catch (IllegalAccessException e) {
                    log.error("执行方法参数异常：{}", e.getMessage(),e);
                } catch (InvocationTargetException e) {
                    log.error("构造方法异常:{}", e.getMessage(),e);
                }
            }
            ok.setData(map);
        }
        return ok;
    }

/*

    public Result getInterFaceMethods(KbParms kbParms) {
        GetOrderNoLineNo no = kbParms.getOrderNoLineNo();
        Result<Object> ok = Result.ok();
        Map<String, Object> map = new HashMap<>();
        Result dailyOutput = dayDataService.getDailyOutput(no);
        map.put("dailyOutput",dailyOutput);
        Result monthData = mouthDataService.getMonthData(no);
        map.put("monthData",monthData);
        Result planFiveDay = ratioService.getProductionPlanFiveDay(no);
        map.put("planFiveDay",planFiveDay);
        Result planAnalysis = analysisService.getPlanAnalysis(no);
        map.put("planAnalysis",planAnalysis);
        Result timeAnalysis = timeAnalysisService.getTimeAnalysis(no);
        map.put("timeAnalysis",timeAnalysis);
        Result output = yieldAnalysisService.getOutputByLineId(no);
        map.put("output",output);
        ok.setData(map);
        return ok;
    }
*/

}
