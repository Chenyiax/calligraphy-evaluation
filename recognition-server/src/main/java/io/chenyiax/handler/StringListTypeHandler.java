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
 * A custom MyBatis type handler for converting between a {@code List<String>} and a database column.
 * This handler is used to map a list of strings to a comma - separated string in the database,
 * and vice versa.
 */
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {
    /**
     * Logger instance for this class, used to log important information and errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(StringListTypeHandler.class);

    /**
     * Sets a non - null parameter to the prepared statement.
     * Converts a {@code List<String>} to a comma - separated string and sets it to the specified position in the prepared statement.
     *
     * @param ps        The prepared statement to which the parameter will be set.
     * @param i         The index of the parameter in the prepared statement.
     * @param parameter The {@code List<String>} to be set as a parameter.
     * @param jdbcType  The JDBC type of the parameter.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        // Convert the List<String> to a comma-separated string and set it to the prepared statement
        ps.setString(i, String.join(",", parameter));
    }

    /**
     * Retrieves a nullable result from the result set using the column name.
     * Converts a comma - separated string from the database to a {@code List<String>}.
     *
     * @param rs         The result set from which the value will be retrieved.
     * @param columnName The name of the column in the result set.
     * @return A {@code List<String>} converted from the database value, or an empty list if the value is null.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // Retrieve the string value from the result set
        String value = rs.getString(columnName);
        // Convert the string to a List<String> or return an empty list if the value is null
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }

    /**
     * Retrieves a nullable result from the result set using the column index.
     * Converts a comma - separated string from the database to a {@code List<String>}.
     *
     * @param rs          The result set from which the value will be retrieved.
     * @param columnIndex The index of the column in the result set.
     * @return A {@code List<String>} converted from the database value, or an empty list if the value is null.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // Retrieve the string value from the result set
        String value = rs.getString(columnIndex);
        // Convert the string to a List<String> or return an empty list if the value is null
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }

    /**
     * Retrieves a nullable result from the callable statement using the column index.
     * Converts a comma - separated string from the database to a {@code List<String>}.
     *
     * @param cs          The callable statement from which the value will be retrieved.
     * @param columnIndex The index of the column in the callable statement.
     * @return A {@code List<String>} converted from the database value, or an empty list if the value is null.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // Retrieve the string value from the callable statement
        String value = cs.getString(columnIndex);
        // Convert the string to a List<String> or return an empty list if the value is null
        return value == null ? Collections.emptyList() : Arrays.asList(value.split(","));
    }
}