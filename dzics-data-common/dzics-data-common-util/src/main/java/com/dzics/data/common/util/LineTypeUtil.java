package com.dzics.data.common.util;

import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.LineTypeProdoctType;
import org.springframework.util.StringUtils;

public class LineTypeUtil {
    public static boolean typtIsOk(String lineType) {
        if (StringUtils.isEmpty(lineType)){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR50);
        }
        if (lineType.equals(LineTypeProdoctType.ONE) || lineType.equals(LineTypeProdoctType.TWO)
                || lineType.equals(LineTypeProdoctType.THREE) || lineType.equals(LineTypeProdoctType.FOUR)) {
            return true;
        }
       throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR50);
    }
}
