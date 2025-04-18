package io.chenyiax.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringListTypeHandler extends BaseTypeHandler<List<String>> {
    private static final Logger logger = LoggerFactory.getLogger(StringListTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {

        ps.setString(i, String.join(",", parameter)); // 将 List 转换为逗号分隔的字符串
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(",")); // 将字符串转换为 List
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }
}