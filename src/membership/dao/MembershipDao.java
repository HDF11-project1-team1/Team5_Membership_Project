package membership.dao;

import common.exception.DataAccessException;

import common.connection.DBConnection;
import common.connection.DBType;
import membership.dto.MembershipCurrentGradeDto;
import membership.dto.MembershipHistoryDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MembershipDao {

    // 1. 전체 고객 VIP 멤버십 등급 현황
    public List<MembershipCurrentGradeDto> selectAllCurrentMembershipGrades() {

        List<MembershipCurrentGradeDto> membershipGradeList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select u.user_id, u.name, u.phone_number, m.membership_id, m.membership_grade " +
                    "from users u join membership m on u.membership_id = m.membership_id " +
                    "order by u.user_id";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MembershipCurrentGradeDto currentGrade = new MembershipCurrentGradeDto();
                currentGrade.setUserId(rs.getInt("user_id"));
                currentGrade.setName(rs.getString("name"));
                currentGrade.setPhoneNumber(rs.getString("phone_number"));
                currentGrade.setMembershipId(rs.getInt("membership_id"));
                currentGrade.setMembershipGrade(rs.getString("membership_grade"));

                membershipGradeList.add(currentGrade);
            }
        } catch (Exception e) {
            throw new DataAccessException("멤버십 정보를 조회하는 중 오류가 발생했습니다.", e);
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return membershipGradeList;
    }

    // 2. 회원별 등급 변경 이력 조회
    public List<MembershipHistoryDto> selectMembershipHistoriesByNameAndBirth(String name, LocalDate birth) {

        List<MembershipHistoryDto> membershipHistoryList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select h.membership_history_id, h.user_id, u.name, h.membership_id, m.membership_grade, " +
                    "h.start_date, h.end_date, h.calculated_amount " +
                    "from membership_history h " +
                    "join users u on h.user_id = u.user_id " +
                    "join membership m on h.membership_id = m.membership_id " +
                    "where u.name = ? and trunc(u.birth) = ? " +
                    "order by h.start_date, h.membership_history_id";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDate(2, java.sql.Date.valueOf(birth));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MembershipHistoryDto membershipHistory = new MembershipHistoryDto();
                membershipHistory.setMembershipHistoryId(rs.getInt("membership_history_id"));
                membershipHistory.setUserId(rs.getInt("user_id"));
                membershipHistory.setName(rs.getString("name"));
                membershipHistory.setMembershipId(rs.getInt("membership_id"));
                membershipHistory.setMembershipGrade(rs.getString("membership_grade"));
                membershipHistory.setStartDate(toLocalDateTime(rs.getTimestamp("start_date")));
                membershipHistory.setEndDate(toLocalDateTime(rs.getTimestamp("end_date")));
                membershipHistory.setCalculateAmount(rs.getLong("calculated_amount"));

                membershipHistoryList.add(membershipHistory);
            }
        } catch (Exception e) {
            throw new DataAccessException("멤버십 정보를 조회하는 중 오류가 발생했습니다.", e);
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return membershipHistoryList;
    }

    // 3. EARLY GREEN 현황 조회
    public List<MembershipCurrentGradeDto> selectEarlyGreenMembers() {

        List<MembershipCurrentGradeDto> earlyGreenList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select u.user_id, u.name, u.phone_number, m.membership_id, m.membership_grade " +
                    "from users u join membership m on u.membership_id = m.membership_id " +
                    "where upper(m.membership_grade) = 'EARLY GREEN' " +
                    "order by u.user_id";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MembershipCurrentGradeDto earlyGreen = new MembershipCurrentGradeDto();
                earlyGreen.setUserId(rs.getInt("user_id"));
                earlyGreen.setName(rs.getString("name"));
                earlyGreen.setPhoneNumber(rs.getString("phone_number"));
                earlyGreen.setMembershipId(rs.getInt("membership_id"));
                earlyGreen.setMembershipGrade(rs.getString("membership_grade"));

                earlyGreenList.add(earlyGreen);
            }
        } catch (Exception e) {
            throw new DataAccessException("멤버십 정보를 조회하는 중 오류가 발생했습니다.", e);
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return earlyGreenList;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}

