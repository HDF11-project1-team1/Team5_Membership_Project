package policy.dao;

import common.connection.DBConnection;
import common.connection.DBType;
import common.exception.DataAccessException;
import master.dto.request.BranchRegisterRequestDto;
import master.dto.request.BrandRegisterRequestDto;
import master.dto.request.LoungeRegisterRequestDto;
import master.dto.request.PaymentRegisterRequestDto;
import membership.dto.request.MembershipRegisterRequestDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultPolicyDao {

    public boolean createDefaultPoliciesForNewBranch(int branchId, BranchRegisterRequestDto requestDto) {
        Connection conn = null;
        try {
            conn = openTransaction();

            insertVipPoliciesForBranch(conn, branchId);
            insertMileagePoliciesForBranch(conn, branchId);
            insertValetPoliciesForBranch(conn, branchId);
            insertFreeParkingPoliciesForBranch(conn, branchId);
            insertLoungePoliciesForBranch(conn, branchId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new DataAccessException("신규 지점 기본 정책을 생성하는 중 오류가 발생했습니다.", e);
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    public boolean createDefaultPoliciesForNewBrand(int brandId, BrandRegisterRequestDto requestDto) {
        Connection conn = null;
        try {
            conn = openTransaction();

            insertMileagePoliciesForBrand(conn, brandId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new DataAccessException("신규 브랜드 기본 정책을 생성하는 중 오류가 발생했습니다.", e);
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    public boolean createDefaultPoliciesForNewPayment(int paymentId, PaymentRegisterRequestDto requestDto) {
        Connection conn = null;
        try {
            conn = openTransaction();

            insertVipPoliciesForPayment(conn, paymentId);
            insertMileagePoliciesForPayment(conn, paymentId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new DataAccessException("신규 결제수단 기본 정책을 생성하는 중 오류가 발생했습니다.", e);
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    public boolean createDefaultPoliciesForNewMembership(int membershipId, MembershipRegisterRequestDto requestDto) {
        Connection conn = null;
        try {
            conn = openTransaction();

            insertValetPoliciesForMembership(conn, membershipId);
            insertFreeParkingPoliciesForMembership(conn, membershipId);
            insertLoungePoliciesForMembership(conn, membershipId);
            insertRewardPolicyForMembership(conn, membershipId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new DataAccessException("신규 멤버십 기본 정책을 생성하는 중 오류가 발생했습니다.", e);
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    public boolean createDefaultPoliciesForNewLounge(int loungeId, LoungeRegisterRequestDto requestDto) {
        Connection conn = null;
        try {
            conn = openTransaction();

            insertLoungePoliciesForLounge(conn, loungeId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new DataAccessException("신규 라운지 기본 정책을 생성하는 중 오류가 발생했습니다.", e);
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    private Connection openTransaction() throws SQLException {
        Connection conn = DBConnection.getConnection(DBType.ORACLE);
        if (conn == null) {
            throw new SQLException("DB connection failed");
        }
        conn.setAutoCommit(false);
        return conn;
    }

    private void insertVipPoliciesForBranch(Connection conn, int branchId) throws SQLException {
        String sql = "INSERT INTO vip_policy (vip_policy_id, branch_id, payment_id, vip_rate) "
                + "SELECT seq_vip_policy.NEXTVAL, ?, p.payment_id, 0 "
                + "FROM payment p "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM vip_policy vp "
                + "    WHERE vp.branch_id = ? AND vp.payment_id = p.payment_id"
                + ")";
        executeUpdate(conn, sql, branchId, branchId);
    }

    private void insertMileagePoliciesForBranch(Connection conn, int branchId) throws SQLException {
        String sql = "INSERT INTO mileage_policy (mileage_policy_id, branch_id, brand_id, payment_id, mileage_rate) "
                + "SELECT seq_mileage_policy.NEXTVAL, ?, b.brand_id, p.payment_id, 0 "
                + "FROM brand b CROSS JOIN payment p "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM mileage_policy mp "
                + "    WHERE mp.branch_id = ? AND mp.brand_id = b.brand_id AND mp.payment_id = p.payment_id"
                + ")";
        executeUpdate(conn, sql, branchId, branchId);
    }

    private void insertValetPoliciesForBranch(Connection conn, int branchId) throws SQLException {
        String sql = "INSERT INTO valet_policy "
                + "(valet_policy_id, branch_id, membership_id, last_year_vip_min_standard, last_year_vip_max_standard, valet_available) "
                + "SELECT seq_valet_policy.NEXTVAL, ?, m.membership_id, NULL, NULL, 0 "
                + "FROM membership m "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM valet_policy vp "
                + "    WHERE vp.branch_id = ? AND vp.membership_id = m.membership_id"
                + ")";
        executeUpdate(conn, sql, branchId, branchId);
    }

    private void insertFreeParkingPoliciesForBranch(Connection conn, int branchId) throws SQLException {
        String sql = "INSERT INTO free_parking_policy "
                + "(free_parking_policy_id, branch_id, membership_id, free_parking_available) "
                + "SELECT seq_free_parking_policy.NEXTVAL, ?, m.membership_id, 0 "
                + "FROM membership m "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM free_parking_policy fp "
                + "    WHERE fp.branch_id = ? AND fp.membership_id = m.membership_id"
                + ")";
        executeUpdate(conn, sql, branchId, branchId);
    }

    private void insertLoungePoliciesForBranch(Connection conn, int branchId) throws SQLException {
        String sql = "INSERT INTO lounge_policy "
                + "(lounge_policy_id, branch_id, lounge_id, membership_id, lounge_available) "
                + "SELECT seq_lounge_policy.NEXTVAL, ?, l.lounge_id, m.membership_id, 0 "
                + "FROM lounge l CROSS JOIN membership m "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM lounge_policy lp "
                + "    WHERE lp.branch_id = ? AND lp.lounge_id = l.lounge_id AND lp.membership_id = m.membership_id"
                + ")";
        executeUpdate(conn, sql, branchId, branchId);
    }

    private void insertMileagePoliciesForBrand(Connection conn, int brandId) throws SQLException {
        String sql = "INSERT INTO mileage_policy (mileage_policy_id, branch_id, brand_id, payment_id, mileage_rate) "
                + "SELECT seq_mileage_policy.NEXTVAL, br.branch_id, ?, p.payment_id, 0 "
                + "FROM branch br CROSS JOIN payment p "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM mileage_policy mp "
                + "    WHERE mp.branch_id = br.branch_id AND mp.brand_id = ? AND mp.payment_id = p.payment_id"
                + ")";
        executeUpdate(conn, sql, brandId, brandId);
    }

    private void insertVipPoliciesForPayment(Connection conn, int paymentId) throws SQLException {
        String sql = "INSERT INTO vip_policy (vip_policy_id, branch_id, payment_id, vip_rate) "
                + "SELECT seq_vip_policy.NEXTVAL, b.branch_id, ?, 0 "
                + "FROM branch b "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM vip_policy vp "
                + "    WHERE vp.branch_id = b.branch_id AND vp.payment_id = ?"
                + ")";
        executeUpdate(conn, sql, paymentId, paymentId);
    }

    private void insertMileagePoliciesForPayment(Connection conn, int paymentId) throws SQLException {
        String sql = "INSERT INTO mileage_policy (mileage_policy_id, branch_id, brand_id, payment_id, mileage_rate) "
                + "SELECT seq_mileage_policy.NEXTVAL, br.branch_id, ba.brand_id, ?, 0 "
                + "FROM branch br CROSS JOIN brand ba "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM mileage_policy mp "
                + "    WHERE mp.branch_id = br.branch_id AND mp.brand_id = ba.brand_id AND mp.payment_id = ?"
                + ")";
        executeUpdate(conn, sql, paymentId, paymentId);
    }

    private void insertValetPoliciesForMembership(Connection conn, int membershipId) throws SQLException {
        String sql = "INSERT INTO valet_policy "
                + "(valet_policy_id, branch_id, membership_id, last_year_vip_min_standard, last_year_vip_max_standard, valet_available) "
                + "SELECT seq_valet_policy.NEXTVAL, b.branch_id, ?, NULL, NULL, 0 "
                + "FROM branch b "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM valet_policy vp "
                + "    WHERE vp.branch_id = b.branch_id AND vp.membership_id = ?"
                + ")";
        executeUpdate(conn, sql, membershipId, membershipId);
    }

    private void insertFreeParkingPoliciesForMembership(Connection conn, int membershipId) throws SQLException {
        String sql = "INSERT INTO free_parking_policy "
                + "(free_parking_policy_id, branch_id, membership_id, free_parking_available) "
                + "SELECT seq_free_parking_policy.NEXTVAL, b.branch_id, ?, 0 "
                + "FROM branch b "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM free_parking_policy fp "
                + "    WHERE fp.branch_id = b.branch_id AND fp.membership_id = ?"
                + ")";
        executeUpdate(conn, sql, membershipId, membershipId);
    }

    private void insertLoungePoliciesForMembership(Connection conn, int membershipId) throws SQLException {
        String sql = "INSERT INTO lounge_policy "
                + "(lounge_policy_id, branch_id, lounge_id, membership_id, lounge_available) "
                + "SELECT seq_lounge_policy.NEXTVAL, b.branch_id, l.lounge_id, ?, 0 "
                + "FROM branch b CROSS JOIN lounge l "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM lounge_policy lp "
                + "    WHERE lp.branch_id = b.branch_id AND lp.lounge_id = l.lounge_id AND lp.membership_id = ?"
                + ")";
        executeUpdate(conn, sql, membershipId, membershipId);
    }

    private void insertRewardPolicyForMembership(Connection conn, int membershipId) throws SQLException {
        String sql = "INSERT INTO reward_policy "
                + "(reward_policy_id, membership_id, offer_standard, base_reward_amount, repeat_unit_score, repeat_reward_amount) "
                + "SELECT seq_reward_policy.NEXTVAL, ?, policy.offer_standard, policy.base_reward_amount, 10000, policy.repeat_reward_amount "
                + "FROM ("
                + "    SELECT 15000 AS offer_standard, 225000 AS base_reward_amount, 0 AS repeat_reward_amount FROM dual "
                + "    UNION ALL "
                + "    SELECT 30000 AS offer_standard, 450000 AS base_reward_amount, 0 AS repeat_reward_amount FROM dual "
                + "    UNION ALL "
                + "    SELECT 40000 AS offer_standard, 600000 AS base_reward_amount, 150000 AS repeat_reward_amount FROM dual "
                + ") policy "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM reward_policy rp "
                + "    WHERE rp.membership_id = ? AND rp.offer_standard = policy.offer_standard"
                + ")";
        executeUpdate(conn, sql, membershipId, membershipId);
    }

    private void insertLoungePoliciesForLounge(Connection conn, int loungeId) throws SQLException {
        String sql = "INSERT INTO lounge_policy "
                + "(lounge_policy_id, branch_id, lounge_id, membership_id, lounge_available) "
                + "SELECT seq_lounge_policy.NEXTVAL, b.branch_id, ?, m.membership_id, 0 "
                + "FROM branch b CROSS JOIN membership m "
                + "WHERE NOT EXISTS ("
                + "    SELECT 1 FROM lounge_policy lp "
                + "    WHERE lp.branch_id = b.branch_id AND lp.lounge_id = ? AND lp.membership_id = m.membership_id"
                + ")";
        executeUpdate(conn, sql, loungeId, loungeId);
    }

    private void executeUpdate(Connection conn, String sql, Object... values) throws SQLException {
        long startTime = System.currentTimeMillis();
        System.out.println("Default policy bulk insert start");
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                Object value = values[i];
                if (value instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) value);
                } else if (value instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) value);
                } else {
                    pstmt.setObject(i + 1, value);
                }
            }
            int count = pstmt.executeUpdate();
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Default policy bulk insert completed: " + count + " rows, " + elapsedTime + "ms");
        }
    }

    private void rollback(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new DataAccessException("기본 정책 생성 롤백 중 오류가 발생했습니다.", e);
        }
    }

    private void resetAutoCommit(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataAccessException("커밋 모드를 복구하는 중 오류가 발생했습니다.", e);
        }
    }
}
