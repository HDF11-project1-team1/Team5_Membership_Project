package membership.dao;

import common.connection.DBType;
import common.jdbc.JdbcTemplate;
import membership.dto.MembershipCurrentGradeDto;
import membership.dto.MembershipHistoryDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MembershipDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DBType.ORACLE);

    // 1. 전체 고객 VIP 멤버십 등급 현황
    public List<MembershipCurrentGradeDto> selectAllCurrentMembershipGrades() {
        String sql = "select u.user_id, u.name, u.phone_number, m.membership_id, m.membership_grade " +
                "from users u join membership m on u.membership_id = m.membership_id " +
                "order by u.user_id";

        return jdbcTemplate.query(sql, this::mapCurrentGrade);
    }

    // 2. 회원별 등급 변경 이력 조회
    public List<MembershipHistoryDto> selectMembershipHistoriesByNameAndBirth(String name, LocalDate birth) {
        String sql = "select h.membership_history_id, h.user_id, u.name, h.membership_id, m.membership_grade, " +
                "h.start_date, h.end_date, h.calculated_amount " +
                "from membership_history h " +
                "join users u on h.user_id = u.user_id " +
                "join membership m on h.membership_id = m.membership_id " +
                "where u.name = ? and trunc(u.birth) = ? " +
                "order by h.start_date, h.membership_history_id";

        return jdbcTemplate.query(sql, pstmt -> {
            pstmt.setString(1, name);
            pstmt.setDate(2, java.sql.Date.valueOf(birth));
        }, this::mapMembershipHistory);
    }

    // 3. EARLY GREEN 현황 조회
    public List<MembershipCurrentGradeDto> selectEarlyGreenMembers() {
        String sql = "select u.user_id, u.name, u.phone_number, m.membership_id, m.membership_grade " +
                "from users u join membership m on u.membership_id = m.membership_id " +
                "where upper(m.membership_grade) = 'EARLY GREEN' " +
                "order by u.user_id";

        return jdbcTemplate.query(sql, this::mapCurrentGrade);
    }

    private MembershipCurrentGradeDto mapCurrentGrade(ResultSet rs) throws SQLException {
        MembershipCurrentGradeDto dto = new MembershipCurrentGradeDto();
        dto.setUserId(rs.getInt("user_id"));
        dto.setName(rs.getString("name"));
        dto.setPhoneNumber(rs.getString("phone_number"));
        dto.setMembershipId(rs.getInt("membership_id"));
        dto.setMembershipGrade(rs.getString("membership_grade"));
        return dto;
    }

    private MembershipHistoryDto mapMembershipHistory(ResultSet rs) throws SQLException {
        MembershipHistoryDto dto = new MembershipHistoryDto();
        dto.setMembershipHistoryId(rs.getInt("membership_history_id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setName(rs.getString("name"));
        dto.setMembershipId(rs.getInt("membership_id"));
        dto.setMembershipGrade(rs.getString("membership_grade"));
        dto.setStartDate(toLocalDateTime(rs.getTimestamp("start_date")));
        dto.setEndDate(toLocalDateTime(rs.getTimestamp("end_date")));
        dto.setCalculateAmount(rs.getLong("calculated_amount"));
        return dto;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
