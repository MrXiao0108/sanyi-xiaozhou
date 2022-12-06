package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dzics.data.appoint.changsha.mom.db.dao.MomMaterialStorageDao;
import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.MOMStorageDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialStorage;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.service.MomMaterialStorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料仓库 服务实现类
 * </p>
 *
 * @author van
 * @since 2022-07-27
 */
@Slf4j
@Service
public class MomMaterialStorageServiceImpl extends ServiceImpl<MomMaterialStorageDao, MomMaterialStorage>
        implements MomMaterialStorageService {

    @Override
    public ResultDto storageHandle(IssueOrderInformation<MOMStorageDto> information) {
        MOMStorageDto dto = information.getTask();
        log.info("MomMaterialStorageServiceImpl [storageHandle] 库存处理{}", JSON.toJSONString(dto));

        List<MOMStorageDto.Material> materialList = dto.getMaterialList();
        ArrayList<MomMaterialStorage> addList = new ArrayList<>();
        ArrayList<MomMaterialStorage> updList = new ArrayList<>();
        materialList.forEach(d -> {
            MomMaterialStorage storage = getOne(Wrappers.<MomMaterialStorage>lambdaQuery()
                    .eq(MomMaterialStorage::getWiporderno, d.getWipOrderNo())
                    .eq(MomMaterialStorage::getProductno, d.getProductNo()));
            if (ObjectUtils.isEmpty(storage)) {
                MomMaterialStorage add = new MomMaterialStorage();
                add.setWiporderno(d.getWipOrderNo());
                add.setProductno(d.getProductNo());
                add.setQuantity(d.getQuantity());
                addList.add(add);
            } else {
                BigDecimal quantity = storage.getQuantity().add(d.getQuantity());
                storage.setQuantity(quantity);
                updList.add(storage);
            }
        });
        if (!CollectionUtils.isEmpty(addList)) {
            this.saveBatch(addList);
        }
        if (!(CollectionUtils.isEmpty(updList))) {
            this.updateBatchById(updList);
        }
        return ResultDto.ok(information.getVersion(), information.getTaskId());
    }

    @Override
    public Map<String, String> wipOrderNoTotal(Collection<String> c) {
        List<MomMaterialStorage> list = this.list(Wrappers.<MomMaterialStorage>lambdaQuery()
                .in(MomMaterialStorage::getWiporderno, c));
        Map<String, List<MomMaterialStorage>> wipMap = list
                .stream()
                .collect(Collectors.groupingBy(MomMaterialStorage::getWiporderno));

        Map<String, String> map = new HashMap<>();
        wipMap.forEach((k, v) -> {
            BigDecimal bigDecimal = v
                    .stream()
                    .map(MomMaterialStorage::getQuantity)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.valueOf(0));
            map.put(k, String.valueOf(bigDecimal));
        });
        return map;
    }
}
