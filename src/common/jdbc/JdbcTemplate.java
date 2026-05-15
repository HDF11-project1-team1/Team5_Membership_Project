package common.jdbc;

import common.connection.DBConnection;
import common.connection.DBType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    public int update(String sql) {
        return update(sql, null);
    }

    public int update(String sql, PreparedStatementSetter setter) {
        try (
                Connection conn = DBConnection.getConnection(DBType.LOCALDB);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            setParameters(pstmt, setter);
            return pstmt.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        return query(sql, null, rowMapper);
    }

    public <T> List<T> query(String sql, PreparedStatementSetter setter, RowMapper<T> rowMapper) {
        List<T> result = new ArrayList<>();

        try (
                Connection conn = DBConnection.getConnection(DBType.LOCALDB);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            setParameters(pstmt, setter);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs));
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        return queryForObject(sql, null, rowMapper);
    }

    public <T> T queryForObject(String sql, PreparedStatementSetter setter, RowMapper<T> rowMapper) {
        List<T> result = query(sql, setter, rowMapper);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public boolean exists(String sql) {
        return exists(sql, null);
    }

    public boolean exists(String sql, PreparedStatementSetter setter) {
        Integer count = queryForObject(sql, setter, rs -> rs.getInt(1));
        return count != null && count > 0;
    }

    private void setParameters(PreparedStatement pstmt, PreparedStatementSetter setter) throws SQLException {
        if (setter != null) {
            setter.setValues(pstmt);
        }
    }
}
