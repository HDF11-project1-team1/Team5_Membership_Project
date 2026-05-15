package benefit.dao;

import benefit.dto.CoffeeHistoryDto;
import benefit.dto.LoungeHistoryDto;
import benefit.dto.ParkingHistoryDto;

import common.connection.DBConnection;
import common.connection.DBType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class BenefitDao {

    // 1. (멤버십명, 지점명, 라운지명)으로 라운지 정책 이용가능 여부 조회
    public boolean findLoungePolicyAvailabilityByName(String membershipGrade, String branchName, String loungeName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean available = false;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "SELECT lp.lounge_available " +
                    "FROM lounge_policy lp " +
                    "JOIN membership m ON lp.membership_id = m.membership_id " +
                    "JOIN branch b ON lp.branch_id = b.branch_id " +
                    "JOIN lounge l ON lp.lounge_id = l.lounge_id " +
                    "WHERE m.membership_grade = ? " +
                    "  AND b.branch_name = ? " +
                    "  AND l.lounge_name = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, membershipGrade);
            pstmt.setString(2, branchName);
            pstmt.setString(3, loungeName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int isAvailable = rs.getInt("lounge_available");
                available = (isAvailable > 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return available;
    }

    // 2. (멤버십명)으로 Cafe-H 제공 횟수 정책 조회
    public int findCafeHPolicyCountByName(String membershipGrade) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "SELECT coffee_count FROM membership WHERE membership_grade = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, membershipGrade);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("coffee_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return count;
    }

}
