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

/**
 * StringListTypeHandler 类用于处理 MyBatis 中 List<String> 类型与 JDBC 类型之间的转换。
 * 它继承自 MyBatis 的 BaseTypeHandler 类，并重写了相关方法以实现自定义的类型转换逻辑。
 */
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

    /**
     * 日志记录器，用于记录该类的相关日志信息。不过当前该字段未被使用。
     */
    private static final Logger logger = LoggerFactory.getLogger(StringListTypeHandler.class);

    /**
     * 当参数不为空时，将 List<String> 类型的参数转换为逗号分隔的字符串，并设置到 PreparedStatement 中。
     *
     * @param ps 用于执行 SQL 语句的 PreparedStatement 对象。
     * @param i 参数在 SQL 语句中的位置索引。
     * @param parameter 要设置的 List<String> 类型的参数。
     * @param jdbcType 参数对应的 JDBC 类型。
     * @throws SQLException 当执行 SQL 操作发生错误时抛出该异常。
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.join(",", parameter));
    }

    /**
     * 从 ResultSet 中根据列名获取可空的结果，并将其转换为 List<String> 类型。
     *
     * @param rs 用于获取查询结果的 ResultSet 对象。
     * @param columnName 要获取数据的列名。
     * @return 转换后的 List<String> 类型的结果，如果结果为 null，则返回空列表。
     * @throws SQLException 当执行 SQL 操作发生错误时抛出该异常。
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }

    /**
     * 从 ResultSet 中根据列索引获取可空的结果，并将其转换为 List<String> 类型。
     *
     * @param rs 用于获取查询结果的 ResultSet 对象。
     * @param columnIndex 要获取数据的列索引。
     * @return 转换后的 List<String> 类型的结果，如果结果为 null，则返回空列表。
     * @throws SQLException 当执行 SQL 操作发生错误时抛出该异常。
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }

    /**
     * 从 CallableStatement 中根据列索引获取可空的结果，并将其转换为 List<String> 类型。
     *
     * @param cs 用于执行存储过程的 CallableStatement 对象。
     * @param columnIndex 要获取数据的列索引。
     * @return 转换后的 List<String> 类型的结果，如果结果为 null，则返回空列表。
     * @throws SQLException 当执行 SQL 操作发生错误时抛出该异常。
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }
}