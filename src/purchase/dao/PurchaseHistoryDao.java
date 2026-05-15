package purchase.dao;

import common.connection.DBConnection;
import common.connection.DBType;
import membership.dto.MembershipDto;
import purchase.dto.PurchaseHistoryDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryDao {

    private static final String SELECT_ALL = "SELECT purchase_history_id, user_id, branch_id, category_id, brand_id, " +
            "       membership_id, payment_id, price, discount_price, discount_rate, " +
            "       purchase_status, generated_date, vip_amount, mileage_amount, final_price " +
            "FROM purchase_history ";

    public List<MembershipDto> selectAllMemberships() {
        List<MembershipDto> list = new ArrayList<>();
        String sql = "SELECT membership_id, membership_grade FROM membership ORDER BY membership_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MembershipDto dto = new MembershipDto();
                dto.setMembershipId(rs.getInt("membership_id"));
                dto.setMembershipGrade(rs.getString("membership_grade"));
                list.add(dto);
            }
        } catch (SQLException e) {
            System.out.println("PurchaseHistoryDao.selectAllMemberships : " + e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return list;
    }

    /**
     * 회원 ID로 구매 이력 조회
     */
    public List<PurchaseHistoryDto> selectByUserId(int userId) {
        List<PurchaseHistoryDto> list = new ArrayList<>();
        String sql = SELECT_ALL +
                "WHERE user_id = ? " +
                "ORDER BY generated_date DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("PurchaseHistoryDao.selectByUserId : " + e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return list;
    }

    /**
     * 멤버십 ID로 구매 이력 조회
     */
    public List<PurchaseHistoryDto> selectByMembershipId(int membershipId) {
        List<PurchaseHistoryDto> list = new ArrayList<>();
        String sql = SELECT_ALL +
                "WHERE membership_id = ? " +
                "ORDER BY generated_date DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, membershipId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("PurchaseHistoryDao.selectByMembershipId : " + e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return list;
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

