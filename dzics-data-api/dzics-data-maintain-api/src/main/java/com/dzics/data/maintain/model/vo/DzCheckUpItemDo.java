package com.dzics.data.maintain.model.vo;

import com.dzics.data.maintain.model.entity.DzCheckUpItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DzCheckUpItemDo extends DzCheckUpItem {

    @ApiModelProperty("巡检类型数组")
    private List<CheckTypeDo>checkTypeList;

}
