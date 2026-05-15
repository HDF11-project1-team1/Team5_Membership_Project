package membership.dao;

import common.jdbc.JdbcTemplate;

public class MembershipDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public boolean existsMembershipId(int membershipId) {
        String sql = "SELECT COUNT(*) FROM membership WHERE membership_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, membershipId));
    }
}
