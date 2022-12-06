package com.dzics.data.appoint.changsha.mom.model.enumeration;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author xnb
 * @date 2022/11/14 0014 15:45
 */
@Data
public class DzicsInsideConverter implements Converter {

    @Override
    public Class supportJavaTypeKey() {
        return null;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String s = String.valueOf(value);
        if(!StringUtils.isEmpty(s)){
            if("1".equals(s)){
                s = "Mom订单下发";
                return new CellData<>(s);
            }
            if("4".equals(s)){
                s = "生产叫料";
                return new CellData<>(s);
            }
            if("17".equals(s)){
                s = "Mom订单工序下发";
                return new CellData<>(s);
            }
            if("103".equals(s)){
                s = "满料拖出";
                return new CellData<>(s);
            }
        }
        return null;
    }
}
