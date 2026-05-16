package purchase.dao;

import common.connection.DBType;
import common.jdbc.JdbcTemplate;
import membership.dto.MembershipDto;
import purchase.dto.PurchaseHistoryDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class PurchaseHistoryDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DBType.ORACLE);

    private static final String SELECT_ALL = "SELECT purchase_history_id, user_id, branch_id, category_id, brand_id, " +
            "       membership_id, payment_id, price, discount_price, discount_rate, " +
            "       purchase_status, generated_date, vip_amount, mileage_amount, final_price " +
            "FROM purchase_history ";

    public List<MembershipDto> selectAllMemberships() {
        String sql = "SELECT membership_id, membership_grade FROM membership ORDER BY membership_id";
        return jdbcTemplate.query(sql, rs -> {
            MembershipDto dto = new MembershipDto();
            dto.setMembershipId(rs.getInt("membership_id"));
            dto.setMembershipGrade(rs.getString("membership_grade"));
            return dto;
        });
    }

    /**
     * 회원 ID로 구매 이력 조회
     */
    public List<PurchaseHistoryDto> selectByUserId(int userId) {
        String sql = SELECT_ALL +
                "WHERE user_id = ? " +
                "ORDER BY generated_date DESC";

        return jdbcTemplate.query(sql, pstmt -> pstmt.setInt(1, userId), this::mapRow);
    }

    /**
     * 멤버십 ID로 구매 이력 조회
     */
    public List<PurchaseHistoryDto> selectByMembershipId(int membershipId) {
        String sql = SELECT_ALL +
                "WHERE membership_id = ? " +
                "ORDER BY generated_date DESC";

        return jdbcTemplate.query(sql, pstmt -> pstmt.setInt(1, membershipId), this::mapRow);
    }

    // ResultSet을 PurchaseHistoryDto로 매핑
    private PurchaseHistoryDto mapRow(ResultSet rs) throws SQLException {
        PurchaseHistoryDto dto = new PurchaseHistoryDto();
        dto.setPurchaseHistoryId(rs.getInt("purchase_history_id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setBranchId(rs.getInt("branch_id"));
        dto.setCategoryId(rs.getInt("category_id"));
        dto.setBrandId(rs.getInt("brand_id"));
        dto.setMembershipId(rs.getInt("membership_id"));
        dto.setPaymentId(rs.getInt("payment_id"));
        dto.setPrice(rs.getInt("price"));
        dto.setDiscountPrice(rs.getInt("discount_price"));
        dto.setDiscountRate(rs.getInt("discount_rate"));
        dto.setPurchaseStatus(rs.getString("purchase_status"));
        Timestamp ts = rs.getTimestamp("generated_date");
        if (ts != null) {
            dto.setGeneratedDate(ts.toLocalDateTime());
        }
        dto.setVipAmount(rs.getInt("vip_amount"));
        dto.setMileageAmount(rs.getInt("mileage_amount"));
        dto.setFinalPrice(rs.getInt("final_price"));
        return dto;
    }
}
