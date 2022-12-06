package com.dzics.data.kanban.changsha.xiaozhou.jingjia.config.shading;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ZhangChengJun
 * Date 2022/1/5.
 * @since
 */
@Component
@MappedTypes(LocalTime.class)
@MappedJdbcTypes(value = JdbcType.TIME, includeNullJdbcType = true)
public class MyLocalTimeTypeHandel extends BaseTypeHandler<LocalTime> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalTime localTime, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, localTime);
    }

    @Override
    public LocalTime getNullableResult(ResultSet rs, String columenName) throws SQLException {
        Object object = rs.getObject(columenName);
        LocalTime dateTime = LocalTime.parse(object.toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        return (dateTime);
    }

    @Override
    public LocalTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object object = rs.getObject(columnIndex);
        LocalTime dateTime = LocalTime.parse(object.toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        return (dateTime);
    }

    @Override
    public LocalTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object object = cs.getObject(columnIndex);
        LocalTime dateTime = LocalTime.parse(object.toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        return (dateTime);
    }
}
