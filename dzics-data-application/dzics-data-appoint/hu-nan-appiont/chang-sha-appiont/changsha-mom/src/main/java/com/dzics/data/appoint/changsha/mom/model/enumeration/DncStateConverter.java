package com.dzics.data.appoint.changsha.mom.model.enumeration;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.dzics.data.appoint.changsha.mom.enums.DNCProgramEnum;

/**
 * @author xnb
 * @date 2022/11/11 0011 15:55
 */
public class DncStateConverter implements Converter {

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
        String obj = String.valueOf(value);
        if(DNCProgramEnum.REQUEST_FAIL.equals(obj)){
            obj = "请求失败";
            return new CellData(obj);
        }
        if(DNCProgramEnum.REQUEST_SUCCESS.equals(obj)){
            obj = "请求成功，未反馈";
            return new CellData(obj);
        }
        if(DNCProgramEnum.CHANGE_SUCCESS.equals(obj)){
            obj = "切换成功";
            return new CellData(obj);
        }
        if(DNCProgramEnum.CHANGE_FAIL.equals(obj)){
            obj = "切换失败";
            return new CellData(obj);
        }
        if(DNCProgramEnum.MANUAL.equals(obj)){
            obj = "人工干预";
            return new CellData(obj);
        }
        return null;
    }
}
