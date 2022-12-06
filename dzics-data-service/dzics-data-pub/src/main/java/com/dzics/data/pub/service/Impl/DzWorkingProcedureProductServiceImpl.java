package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.pub.db.dao.DzWorkingProcedureProductDao;
import com.dzics.data.pub.model.entity.DzWorkingProcedureProduct;
import com.dzics.data.pub.model.vo.WorkingProcedures;
import com.dzics.data.pub.service.DzWorkingProcedureProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工序-工件关联关系表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Service
public class DzWorkingProcedureProductServiceImpl extends ServiceImpl<DzWorkingProcedureProductDao, DzWorkingProcedureProduct> implements DzWorkingProcedureProductService {
    @Autowired
    private DzWorkingProcedureProductDao dzWorkingProcedureMapper;

    @Override
    public List<WorkingProcedures> getWorkingProcedures() {
        return dzWorkingProcedureMapper.getWorkingProcedures();
    }
}
