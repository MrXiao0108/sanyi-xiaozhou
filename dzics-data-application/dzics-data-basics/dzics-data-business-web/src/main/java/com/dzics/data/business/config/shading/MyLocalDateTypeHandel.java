package com.dzics.data.business.config.shading;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author ZhangChengJun
 * Date 2022/1/5.
 * @since
 */
@Component
@MappedTypes(LocalDate.class)
@MappedJdbcTypes(value = JdbcType.DATE, includeNullJdbcType = true)
public class MyLocalDateTypeHandel extends BaseTypeHandler<LocalDate> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columenName) throws SQLException {
        Object object = rs.getObject(columenName);
        LocalDate dateTime = LocalDate.parse(object.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return (dateTime);
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object object = rs.getObject(columnIndex);
        LocalDate dateTime = LocalDate.parse(object.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return (dateTime);
    }

    @Override
    public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object object = cs.getObject(columnIndex);
        LocalDate dateTime = LocalDate.parse(object.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return (dateTime);
    }
}
