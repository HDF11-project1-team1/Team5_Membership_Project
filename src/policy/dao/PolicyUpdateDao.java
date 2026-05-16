package policy.dao;

import common.connection.DBConnection;
import common.connection.DBType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;

public class PolicyUpdateDao {

    public int updateVipRate(List<Integer> branchIds, List<Integer> paymentIds, double vipRate) {
        String sql = "UPDATE vip_policy "
                + "SET vip_rate = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND payment_id IN (" + placeholders(paymentIds.size()) + ")";

        try (
                Connection conn = DBConnection.getConnection(DBType.LOCALDB);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            int parameterIndex = 1;
            pstmt.setDouble(parameterIndex++, vipRate);

            for (Integer branchId : branchIds) {
                pstmt.setInt(parameterIndex++, branchId);
            }
            for (Integer paymentId : paymentIds) {
                pstmt.setInt(parameterIndex++, paymentId);
            }

            return pstmt.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateMileageRate(List<Integer> branchIds, List<Integer> brandIds, List<Integer> paymentIds, double mileageRate) {
        String sql = "UPDATE mileage_policy "
                + "SET mileage_rate = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND brand_id IN (" + placeholders(brandIds.size()) + ") "
                + "AND payment_id IN (" + placeholders(paymentIds.size()) + ")";

        try (
                Connection conn = DBConnection.getConnection(DBType.LOCALDB);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            int parameterIndex = 1;
            pstmt.setDouble(parameterIndex++, mileageRate);

            for (Integer branchId : branchIds) {
                pstmt.setInt(parameterIndex++, branchId);
            }
            for (Integer brandId : brandIds) {
                pstmt.setInt(parameterIndex++, brandId);
            }
            for (Integer paymentId : paymentIds) {
                pstmt.setInt(parameterIndex++, paymentId);
            }

            return pstmt.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateFreeParkingAvailable(List<Integer> branchIds, List<Integer> membershipIds, boolean available) {
        String sql = "UPDATE free_parking_policy "
                + "SET free_parking_available = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND membership_id IN (" + placeholders(membershipIds.size()) + ")";

        try (
                Connection conn = DBConnection.getConnection(DBType.LOCALDB);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            int parameterIndex = 1;
            pstmt.setInt(parameterIndex++, toNumber(available));

            for (Integer branchId : branchIds) {
                pstmt.setInt(parameterIndex++, branchId);
            }
            for (Integer membershipId : membershipIds) {
                pstmt.setInt(parameterIndex++, membershipId);
            }

            return pstmt.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateLoungeAvailable(List<Integer> branchIds, List<Integer> loungeIds, List<Integer> membershipIds, boolean available) {
        String sql = "UPDATE lounge_policy "
                + "SET lounge_available = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND lounge_id IN (" + placeholders(loungeIds.size()) + ") "
                + "AND membership_id IN (" + placeholders(membershipIds.size()) + ")";

        try (
                Connection conn = DBConnection.getConnection(DBType.LOCALDB);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            int parameterIndex = 1;
            pstmt.setInt(parameterIndex++, toNumber(available));

            for (Integer branchId : branchIds) {
                pstmt.setInt(parameterIndex++, branchId);
            }
            for (Integer loungeId : loungeIds) {
                pstmt.setInt(parameterIndex++, loungeId);
            }
            for (Integer membershipId : membershipIds) {
                pstmt.setInt(parameterIndex++, membershipId);
            }

            return pstmt.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateValetPolicy(List<Integer> branchIds, List<Integer> membershipIds, int minStandard, int maxStandard, boolean available) {
        String sql = "UPDATE valet_policy "
                + "SET last_year_vip_min_standard = ?, "
                + "last_year_vip_max_standard = ?, "
                + "valet_available = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND membership_id IN (" + placeholders(membershipIds.size()) + ")";

        try (
                Connection conn = DBConnection.getConnection(DBType.LOCALDB);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            int parameterIndex = 1;
            pstmt.setInt(parameterIndex++, minStandard);
            pstmt.setInt(parameterIndex++, maxStandard);
            pstmt.setInt(parameterIndex++, toNumber(available));

            for (Integer branchId : branchIds) {
                pstmt.setInt(parameterIndex++, branchId);
            }
            for (Integer membershipId : membershipIds) {
                pstmt.setInt(parameterIndex++, membershipId);
            }

            return pstmt.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private String placeholders(int count) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < count; i++) {
            joiner.add("?");
        }
        return joiner.toString();
    }

    private int toNumber(boolean value) {
        return value ? 1 : 0;
    }
}
