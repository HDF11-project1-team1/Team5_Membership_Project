package purchase.service;

import membership.dto.MembershipDto;
import purchase.dao.PurchaseHistoryDao;
import purchase.dto.PurchaseHistoryDto;
import common.connection.DBConnection;
import common.connection.DBType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseService {

    private final PurchaseHistoryDao purchaseHistoryDao = new PurchaseHistoryDao();

    // ── 1. 회원 ID로 구매 이력 조회 ────────────────────────────────────
    public List<PurchaseHistoryDto> findPurchaseHistoryByUserId(int userId) {
        return purchaseHistoryDao.selectByUserId(userId);
    }

    // ── 2. 멤버십 ID로 구매 이력 조회 ──────────────────────────────────
    public List<PurchaseHistoryDto> findPurchaseHistoryByMembershipId(int membershipId) {
        return purchaseHistoryDao.selectByMembershipId(membershipId);
    }

    // ── 멤버십 전체 목록 조회 (번호 선택 UI용) ─────────────────────────
    public List<MembershipDto> findAllMemberships() {
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
            System.out.println("PurchaseService.getAllMemberships : " + e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return list;
    }
}
