package com.dzics.data.common.base.vo;

import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/5.
 */
@Data
@ApiModel(value = "通用返回信息", description = "通用数据返回信息")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码 " +
            "0 正常 " +
            "201账户禁用" +
            "403用户或密码错误" +
            "404用户名不存在" +
            "405用户类型错误" +
            "407参数异常" +
            "408token签名错误" +
            "401无权限 " +
            "402请重新获取token " +
            "406重新登录" +
            "500系统异常")
    private Integer code;
    /**
     * 提示信息
     */
    @ApiModelProperty(value = "提示信息")
    private String msg;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Long count = 0L;

    /**
     * 返回数据对象
     */
    @ApiModelProperty(value = "常用返回数据对象")
    private T data;

    @ApiModelProperty(value = "返回日期对象")
    private List<String> dateList;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result() {

    }

    public static <T> Result<T> OK(T data) {
        Result<T> m = new Result<T>();
        m.setCode(CustomExceptionType.OK.getCode());
        m.setMsg(CustomExceptionType.OK.getTypeDesc());
        m.setData(data);
        return m;
    }

    public static <T> Result<T> OK(T data, List<String> dateList) {
        Result<T> m = new Result<T>();
        m.setCode(CustomExceptionType.OK.getCode());
        m.setMsg(CustomExceptionType.OK.getTypeDesc());
        m.setData(data);
        m.setDateList(dateList);
        return m;
    }

    public Result(CustomExceptionType ok, T data, long total) {
        this.msg = ok.getTypeDesc();
        this.code = ok.getCode();
        this.data = data;
        this.count = total;
    }

    /**
     * 请求出现异常时的响应数据封装
     *
     * @param e
     * @return
     */
    public static <T> Result<T> error(CustomException e) {
        Result<T> resultBean = new Result<T>();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getMessage());
        return resultBean;
    }

    public static Result<String> error(String msg) {
        Result<String> resultBean = new Result<>();
        resultBean.setCode(CustomExceptionType.Parameter_Exception.getCode());
        resultBean.setMsg(msg);
        return resultBean;
    }

    public static <T> Result<T> error(CustomExceptionType e, CustomResponseCode responseCode) {
        Result<T> resultBean = new Result<T>();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(responseCode.getChinese());
        return resultBean;
    }

    public static <T> Result<T> error(CustomExceptionType e) {
        Result<T> resultBean = new Result<T>();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getTypeDesc());
        return resultBean;
    }

    public Result(CustomExceptionType type) {
        this.code = type.getCode();
        this.msg = type.getTypeDesc();
    }

    public Result(CustomExceptionType type, String msg) {
        this.code = type.getCode();
        this.msg = msg;
    }


    public static <T> Result<T> ok() {
        Result<T> m = new Result<T>();
        m.setCode(CustomExceptionType.OK.getCode());
        m.setMsg(CustomExceptionType.OK.getTypeDesc());
        return m;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> m = new Result<T>();
        m.setCode(CustomExceptionType.OK.getCode());
        m.setMsg(CustomExceptionType.OK.getTypeDesc());
        m.setData(data);
        return m;
    }

    public static <T> Result<T> ok(T data, Long count) {
        Result<T> m = new Result<T>();
        m.setCode(CustomExceptionType.OK.getCode());
        m.setMsg(CustomExceptionType.OK.getTypeDesc());
        m.setData(data);
        m.setCount(count);
        return m;
    }

    public Result(CustomExceptionType type, T data) {
        this.code = type.getCode();
        this.msg = type.getTypeDesc();
        this.data = data;
    }
}
