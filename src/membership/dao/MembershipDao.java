package membership.dao;

import common.jdbc.JdbcTemplate;
import membership.dto.MembershipDto;

import java.util.List;

public class MembershipDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int insertMembership(MembershipDto membershipDto) {
        return insertMembershipAndReturnId(membershipDto) > 0 ? 1 : 0;
    }

    public int insertMembershipAndReturnId(MembershipDto membershipDto) {
        Integer membershipId = jdbcTemplate.queryForObject("SELECT seq_membership.NEXTVAL FROM dual", rs -> rs.getInt(1));
        if (membershipId == null) {
            return 0;
        }

        String sql = "INSERT INTO membership "
                + "(membership_id, membership_grade, min_amount, max_discount, basic_discount, special_discount_amount, coffee_count) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, membershipId);
            pstmt.setString(2, membershipDto.getMembershipGrade());
            pstmt.setInt(3, membershipDto.getMinAmount());
            pstmt.setInt(4, membershipDto.getMaxDiscount());
            pstmt.setInt(5, membershipDto.getBasicDiscount());
            pstmt.setInt(6, membershipDto.getSpecialDiscountAmount());
            pstmt.setInt(7, membershipDto.getCoffeeCount());
        });

        return result > 0 ? membershipId : 0;
    }

    public List<MembershipDto> selectAllMemberships() {
        String sql = "SELECT membership_id, membership_grade, min_amount, max_discount, basic_discount, "
                + "special_discount_amount, coffee_count FROM membership ORDER BY membership_id";
        return jdbcTemplate.query(sql, rs -> new MembershipDto(
                rs.getInt("membership_id"),
                rs.getString("membership_grade"),
                rs.getInt("min_amount"),
                rs.getInt("max_discount"),
                rs.getInt("basic_discount"),
                rs.getInt("special_discount_amount"),
                rs.getInt("coffee_count")
        ));
    }

    public boolean existsMembershipId(int membershipId) {
        String sql = "SELECT COUNT(*) FROM membership WHERE membership_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, membershipId));
    }
}
