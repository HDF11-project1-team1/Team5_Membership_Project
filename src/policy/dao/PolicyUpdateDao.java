package policy.dao;

import common.jdbc.JdbcTemplate;

import java.util.List;
import java.util.StringJoiner;

public class PolicyUpdateDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int updateVipRate(List<Integer> branchIds, List<Integer> paymentIds, double vipRate) {
        String sql = "UPDATE vip_policy "
                + "SET vip_rate = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND payment_id IN (" + placeholders(paymentIds.size()) + ")";

        return jdbcTemplate.update(sql, pstmt -> {
            int parameterIndex = 1;
            pstmt.setDouble(parameterIndex++, vipRate);
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            setIntValues(pstmt, parameterIndex, paymentIds);
        });
    }

    public int updateMileageRate(List<Integer> branchIds, List<Integer> brandIds, List<Integer> paymentIds, double mileageRate) {
        String sql = "UPDATE mileage_policy "
                + "SET mileage_rate = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND brand_id IN (" + placeholders(brandIds.size()) + ") "
                + "AND payment_id IN (" + placeholders(paymentIds.size()) + ")";

        return jdbcTemplate.update(sql, pstmt -> {
            int parameterIndex = 1;
            pstmt.setDouble(parameterIndex++, mileageRate);
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            parameterIndex = setIntValues(pstmt, parameterIndex, brandIds);
            setIntValues(pstmt, parameterIndex, paymentIds);
        });
    }

    public int updateFreeParkingAvailable(List<Integer> branchIds, List<Integer> membershipIds, boolean available) {
        String sql = "UPDATE free_parking_policy "
                + "SET free_parking_available = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND membership_id IN (" + placeholders(membershipIds.size()) + ")";

        return jdbcTemplate.update(sql, pstmt -> {
            int parameterIndex = 1;
            pstmt.setInt(parameterIndex++, toNumber(available));
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            setIntValues(pstmt, parameterIndex, membershipIds);
        });
    }

    public int updateLoungeAvailable(List<Integer> branchIds, List<Integer> loungeIds, List<Integer> membershipIds, boolean available) {
        String sql = "UPDATE lounge_policy "
                + "SET lounge_available = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND lounge_id IN (" + placeholders(loungeIds.size()) + ") "
                + "AND membership_id IN (" + placeholders(membershipIds.size()) + ")";

        return jdbcTemplate.update(sql, pstmt -> {
            int parameterIndex = 1;
            pstmt.setInt(parameterIndex++, toNumber(available));
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            parameterIndex = setIntValues(pstmt, parameterIndex, loungeIds);
            setIntValues(pstmt, parameterIndex, membershipIds);
        });
    }

    public int updateValetPolicy(List<Integer> branchIds, List<Integer> membershipIds, int minStandard, int maxStandard, boolean available) {
        String sql = "UPDATE valet_policy "
                + "SET last_year_vip_min_standard = ?, "
                + "last_year_vip_max_standard = ?, "
                + "valet_available = ? "
                + "WHERE branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND membership_id IN (" + placeholders(membershipIds.size()) + ")";

        return jdbcTemplate.update(sql, pstmt -> {
            int parameterIndex = 1;
            pstmt.setInt(parameterIndex++, minStandard);
            pstmt.setInt(parameterIndex++, maxStandard);
            pstmt.setInt(parameterIndex++, toNumber(available));
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            setIntValues(pstmt, parameterIndex, membershipIds);
        });
    }

    private int setIntValues(java.sql.PreparedStatement pstmt, int parameterIndex, List<Integer> values) throws java.sql.SQLException {
        for (Integer value : values) {
            pstmt.setInt(parameterIndex++, value);
        }
        return parameterIndex;
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
