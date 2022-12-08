package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.model.vo.ActiveTip.ActiveTipsVo;
import com.dzics.data.business.service.ActiveTipsService;
import com.dzics.data.tool.model.entity.DzToolInfo;
import com.dzics.data.tool.service.DzToolInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author xnb
 * @date 2022/12/6 0006 14:43
 */
@Slf4j
@Service
public class ActiveTipsServiceImpl implements ActiveTipsService {

    @Autowired
    private DzToolInfoService toolInfoService;

    @Override
    public List<ActiveTipsVo> getActiveTipsVo() {
        List<ActiveTipsVo>activeTipsVos=new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //刀具寿命
        List<DzToolInfo> life = toolInfoService.getBaseMapper().selectList(new QueryWrapper<DzToolInfo>().eq("tool_life", "100"));
        if(!CollectionUtils.isEmpty(life)){
            for(DzToolInfo s : life){
                ActiveTipsVo activeTipsVo = new ActiveTipsVo();
                activeTipsVo.setId(s.getId());
                activeTipsVo.setDataTime(simpleDateFormat.format(s.getUpdateTime()));
                activeTipsVo.setMessage(s.getToolNo()+"刀具寿命到期，请及时更换刀具");
                activeTipsVo.setModelType("2");
                activeTipsVos.add(activeTipsVo);
            }
        }
        //时间降序排序
        activeTipsVos.sort(Comparator.comparing(ActiveTipsVo::getDataTime).reversed());
        return activeTipsVos;
    }
}
