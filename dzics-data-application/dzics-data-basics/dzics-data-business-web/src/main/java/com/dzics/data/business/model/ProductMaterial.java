package com.dzics.data.business.model;

import com.dzics.data.pms.model.entity.DzMaterial;
import com.dzics.data.pms.model.entity.DzProduct;
import lombok.Data;

import java.util.List;

/**
 * @Classname ProductMaterial
 * @Description 描述
 * @Date 2022/2/16 19:35
 * @Created by NeverEnd
 */
@Data
public class ProductMaterial extends DzProduct {
    private List<DzMaterial> materialList;
}
