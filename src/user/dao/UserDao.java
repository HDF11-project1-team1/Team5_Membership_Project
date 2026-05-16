package user.dao;

import common.connection.DBConnection;
import common.connection.DBType;
import common.exception.DataAccessException;
import common.jdbc.JdbcTemplate;
import membership.dto.MembershipDto;
import user.dto.UserDto;
import user.dto.UserTotalInfoDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DBType.ORACLE);

    // 1. 전체 회원 조회
    public List<UserDto> selectAllUsers() {
        String sql = "select user_id, membership_id, name, gender, phone_number, birth, card_number, card_period, employee_yn from users";
        return jdbcTemplate.query(sql, this::mapUser);
    }

    // 2. 회원 상세 조회 (기본정보 + 상세정보)
    public UserTotalInfoDto selectUserDetailByNameAndBirth(String name, LocalDate birth) {
        String sql = "select u.user_id, u.membership_id, u.name, u.gender, u.phone_number, u.birth, u.card_number, " +
                "u.card_period, u.employee_yn, d.vip_amount, d.mileage_amount, d.total_reward_amount, " +
                "d.remain_special_discount_amount, d.remain_coffee, d.visit_date_count, d.purchase_date_count " +
                "from users u join user_detail d on u.user_id = d.user_id " +
                "where u.name = ? and trunc(u.birth) = ?";

        return jdbcTemplate.queryForObject(sql, pstmt -> {
            pstmt.setString(1, name);
            pstmt.setDate(2, java.sql.Date.valueOf(birth));
        }, this::mapUserTotalInfo);
    }

    // pre 3. 멤버십 종류 조회
    public List<MembershipDto> selectAllMemberships() {
        String sql = "select membership_id, membership_grade, min_amount, max_discount, basic_discount, " +
                "special_discount_amount, coffee_count from membership order by membership_id";

        return jdbcTemplate.query(sql, rs -> {
            MembershipDto membership = new MembershipDto();
            membership.setMembershipId(rs.getInt("membership_id"));
            membership.setMembershipGrade(rs.getString("membership_grade"));
            membership.setMinAmount(rs.getInt("min_amount"));
            membership.setMaxDiscount(rs.getInt("max_discount"));
            membership.setBasicDiscount(rs.getInt("basic_discount"));
            membership.setSpecialDiscountAmount(rs.getInt("special_discount_amount"));
            membership.setCoffeeCount(rs.getInt("coffee_count"));
            return membership;
        });
    }

    // 3. 멤버십별 회원 조회
    public List<UserDto> selectUsersByMembershipId(int membershipId) {
        String sql = "select user_id, membership_id, name, gender, phone_number, birth, card_number, card_period, employee_yn " +
                "from users where membership_id = ? order by user_id";

        return jdbcTemplate.query(sql, pstmt -> pstmt.setInt(1, membershipId), this::mapUser);
    }

    // 4. 신규 회원 등록
    // users와 membership_history를 함께 생성해야 하므로 명시적 트랜잭션 유지
    public int insertUser(UserDto user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            user.setUserId(getNextSequenceValue(conn, "seq_users"));

            if (user.getMembershipId() <= 0) {
                user.setMembershipId(findDefaultMembershipId(conn));
            }

            String userSql = "insert into users (user_id, membership_id, name, gender, phone_number, birth, card_number, card_period, employee_yn) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(userSql);
            pstmt.setInt(1, user.getUserId());
            pstmt.setInt(2, user.getMembershipId());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getGender());
            pstmt.setString(5, user.getPhoneNumber());
            setTimestamp(pstmt, 6, user.getBirth());
            pstmt.setString(7, user.getCardNumber());
            setTimestamp(pstmt, 8, user.getCardPeriod());
            pstmt.setInt(9, user.isEmployeeYn() ? 1 : 0);
            int result = pstmt.executeUpdate();
            DBConnection.close(pstmt);
            pstmt = null;

            LocalDateTime endDate = LocalDate.now()
                    .withMonth(12)
                    .withDayOfMonth(31)
                    .atStartOfDay();
            String historySql = "insert into membership_history (membership_history_id, user_id, membership_id, start_date, end_date, calculated_amount) " +
                    "values (seq_membership_history.nextval, ?, ?, sysdate, ?, 0)";
            pstmt = conn.prepareStatement(historySql);
            pstmt.setInt(1, user.getUserId());
            pstmt.setInt(2, user.getMembershipId());
            setTimestamp(pstmt, 3, endDate);
            pstmt.executeUpdate();

            conn.commit();
            return result;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception rollbackException) {
                throw new DataAccessException("회원 등록 롤백 중 오류가 발생했습니다.", rollbackException);
            }
            throw new DataAccessException("회원을 등록하는 중 오류가 발생했습니다.", e);
        } finally {
            DBConnection.close(pstmt);
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    // 5. 회원 정보 수정
    public int updateUser(UserDto user) {
        String sql = "update users set membership_id = ?, name = ?, gender = ?, phone_number = ?, birth = ?, " +
                "card_number = ?, card_period = ?, employee_yn = ? where user_id = ?";

        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, user.getMembershipId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getGender());
            pstmt.setString(4, user.getPhoneNumber());
            setTimestamp(pstmt, 5, user.getBirth());
            pstmt.setString(6, user.getCardNumber());
            setTimestamp(pstmt, 7, user.getCardPeriod());
            pstmt.setInt(8, user.isEmployeeYn() ? 1 : 0);
            pstmt.setInt(9, user.getUserId());
        });
    }

    private UserDto mapUser(ResultSet rs) throws SQLException {
        UserDto user = new UserDto();
        user.setUserId(rs.getInt("user_id"));
        user.setMembershipId(rs.getInt("membership_id"));
        user.setName(rs.getString("name"));
        user.setGender(rs.getString("gender"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setBirth(toLocalDateTime(rs.getTimestamp("birth")));
        user.setCardNumber(rs.getString("card_number"));
        user.setCardPeriod(toLocalDateTime(rs.getTimestamp("card_period")));
        user.setEmployeeYn(rs.getInt("employee_yn") == 1);
        return user;
    }

    private UserTotalInfoDto mapUserTotalInfo(ResultSet rs) throws SQLException {
        UserTotalInfoDto user = new UserTotalInfoDto();
        user.setUserId(rs.getInt("user_id"));
        user.setMembershipId(rs.getInt("membership_id"));
        user.setName(rs.getString("name"));
        user.setGender(rs.getString("gender"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setBirth(toLocalDateTime(rs.getTimestamp("birth")));
        user.setCardNumber(rs.getString("card_number"));
        user.setCardPeriod(toLocalDateTime(rs.getTimestamp("card_period")));
        user.setEmployeeYn(rs.getInt("employee_yn") == 1);
        user.setVipAmount(rs.getInt("vip_amount"));
        user.setMileageAmount(rs.getInt("mileage_amount"));
        user.setTotalRewardAmount(rs.getInt("total_reward_amount"));
        user.setRemainSpecialDiscountAmount(rs.getInt("remain_special_discount_amount"));
        user.setRemainCoffee(rs.getInt("remain_coffee"));
        user.setVisitDateCount(rs.getInt("visit_date_count"));
        user.setPurchaseDateCount(rs.getInt("purchase_date_count"));
        return user;
    }

    private int getNextSequenceValue(Connection conn, String sequenceName) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "select " + sequenceName + ".nextval next_id from dual";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }

        return 1;
    }

    // 기본 멤버십 등급 조회
    private int findDefaultMembershipId(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "select membership_id from (select membership_id from membership order by min_amount) where rownum = 1";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("membership_id");
            }
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }

        return 1;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private void setTimestamp(PreparedStatement pstmt, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, Types.TIMESTAMP);
            return;
        }

        pstmt.setTimestamp(index, Timestamp.valueOf(value));
    }

    private void resetAutoCommit(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                throw new DataAccessException("커밋 모드를 복구하는 중 오류가 발생했습니다.", e);
            }
        }
    }
}
