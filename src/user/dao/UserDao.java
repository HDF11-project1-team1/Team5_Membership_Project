package user.dao;

import common.connection.DBConnection;
import common.connection.DBType;
import membership.dto.MembershipDto;
import membership.dto.MembershipHistoryDto;
import user.dto.UserDto;
import user.dto.UserTotalInfoDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // 1. 전체 회원 조회
    public List<UserDto> findAllUsers() {

        List<UserDto> userList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select user_id, membership_id, name, gender, phone_number, birth, card_number, card_period, employee_yn from users";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                userList.add(mapUser(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return userList;
    }

    // 2. 회원 상세 조회 (기본정보 + 상세정보)
    public List<UserTotalInfoDto> findAllUserDetails() {

        List<UserTotalInfoDto> userTotalInfoList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select u.user_id, u.membership_id, u.name, u.gender, u.phone_number, u.birth, u.card_number, " +
                    "u.card_period, u.employee_yn, d.vip_amount, d.mileage_amount, d.total_reward_amount, " +
                    "d.remain_special_discount_amount, d.remain_coffee, d.visit_date_count, d.purchase_date_count " +
                    "from users u join user_detail d on u.user_id = d.user_id";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                userTotalInfoList.add(mapUserTotalInfo(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return userTotalInfoList;
    }

    public UserTotalInfoDto findUserDetailByNameAndBirth(String name, LocalDate birth) {

        UserTotalInfoDto user = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select u.user_id, u.membership_id, u.name, u.gender, u.phone_number, u.birth, u.card_number, " +
                    "u.card_period, u.employee_yn, d.vip_amount, d.mileage_amount, d.total_reward_amount, " +
                    "d.remain_special_discount_amount, d.remain_coffee, d.visit_date_count, d.purchase_date_count " +
                    "from users u join user_detail d on u.user_id = d.user_id " +
                    "where u.name = ? and trunc(u.birth) = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDate(2, java.sql.Date.valueOf(birth));
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = mapUserTotalInfo(rs);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return user;
    }

    // pre 3. 멤버십 종류 조회
    public UserTotalInfoDto findUserDetailByUserId(int userId) {

        UserTotalInfoDto user = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select u.user_id, u.membership_id, u.name, u.gender, u.phone_number, u.birth, u.card_number, " +
                    "u.card_period, u.employee_yn, d.vip_amount, d.mileage_amount, d.total_reward_amount, " +
                    "d.remain_special_discount_amount, d.remain_coffee, d.visit_date_count, d.purchase_date_count " +
                    "from users u join user_detail d on u.user_id = d.user_id " +
                    "where u.user_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = mapUserTotalInfo(rs);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return user;
    }

    public List<MembershipDto> findAllMemberships() {

        List<MembershipDto> membershipList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select membership_id, membership_grade, min_amount, max_discount, basic_discount, " +
                    "special_discount_amount, coffee_count from membership order by membership_id";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MembershipDto membership = new MembershipDto();
                membership.setMembershipId(rs.getInt("membership_id"));
                membership.setMembershipGrade(rs.getString("membership_grade"));
                membership.setMinAmount(rs.getInt("min_amount"));
                membership.setMaxDiscount(rs.getInt("max_discount"));
                membership.setBasicDiscount(rs.getInt("basic_discount"));
                membership.setSpecialDiscountAmount(rs.getInt("special_discount_amount"));
                membership.setCoffeeCount(rs.getInt("coffee_count"));

                membershipList.add(membership);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return membershipList;
    }

    // 3. 멤버십별 회원 조회
    public List<UserDto> findUsersByMembershipId(int membershipId) {

        List<UserDto> userList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "select user_id, membership_id, name, gender, phone_number, birth, card_number, card_period, employee_yn " +
                    "from users where membership_id = ? order by user_id";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, membershipId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                userList.add(mapUser(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return userList;
    }

    // 4. 신규 회원 등록
    public int registerUser(UserDto user) {

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

            String historySql = "insert into membership_history (membership_history_id, user_id, membership_id, start_date, end_date, calculated_amount) " +
                    "values (seq_membership_history.nextval, ?, ?, sysdate, null, 0)";
            pstmt = conn.prepareStatement(historySql);
            pstmt.setInt(1, user.getUserId());
            pstmt.setInt(2, user.getMembershipId());
            pstmt.executeUpdate();

            conn.commit();
            return result;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(pstmt);
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }

        return 0;
    }

    // 5. 회원 정보 수정
    public int updateUser(UserDto user) {

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "update users set membership_id = ?, name = ?, gender = ?, phone_number = ?, birth = ?, " +
                    "card_number = ?, card_period = ?, employee_yn = ? where user_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getMembershipId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getGender());
            pstmt.setString(4, user.getPhoneNumber());
            setTimestamp(pstmt, 5, user.getBirth());
            pstmt.setString(6, user.getCardNumber());
            setTimestamp(pstmt, 7, user.getCardPeriod());
            pstmt.setInt(8, user.isEmployeeYn() ? 1 : 0);
            pstmt.setInt(9, user.getUserId());

            return pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }

        return 0;
    }

    private UserDto mapUser(ResultSet rs) throws Exception {
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

    private UserTotalInfoDto mapUserTotalInfo(ResultSet rs) throws Exception {
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

    private int getNextSequenceValue(Connection conn, String sequenceName) throws Exception {
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

    private int findDefaultMembershipId(Connection conn) throws Exception {
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

    private void setTimestamp(PreparedStatement pstmt, int index, LocalDateTime value) throws Exception {
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
                System.out.println(e.getMessage());
            }
        }
    }
}
