package policy.dao;

import common.jdbc.JdbcTemplate;
import policy.dto.PolicyOptionDto;
import policy.dto.PolicyPreviewDto;

import java.util.List;
import java.util.StringJoiner;

public class PolicyUpdateDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    // *** 정책 관리 수정 및 고도화 ***
    public List<PolicyOptionDto> selectBranches() {
        String sql = "SELECT branch_id, branch_name FROM branch ORDER BY branch_id";
        return jdbcTemplate.query(sql, rs -> new PolicyOptionDto(
                rs.getInt("branch_id"),
                rs.getString("branch_name")
        ));
    }

    public List<PolicyOptionDto> selectBrands() {
        String sql = "SELECT brand_id, brand_name FROM brand ORDER BY brand_id";
        return jdbcTemplate.query(sql, rs -> new PolicyOptionDto(
                rs.getInt("brand_id"),
                rs.getString("brand_name")
        ));
    }

    public List<PolicyOptionDto> selectPayments() {
        String sql = "SELECT payment_id, payment_type FROM payment ORDER BY payment_id";
        return jdbcTemplate.query(sql, rs -> new PolicyOptionDto(
                rs.getInt("payment_id"),
                rs.getString("payment_type")
        ));
    }

    public List<PolicyOptionDto> selectMemberships() {
        String sql = "SELECT membership_id, membership_grade FROM membership ORDER BY membership_id";
        return jdbcTemplate.query(sql, rs -> new PolicyOptionDto(
                rs.getInt("membership_id"),
                rs.getString("membership_grade")
        ));
    }

    public List<PolicyOptionDto> selectLounges() {
        String sql = "SELECT lounge_id, lounge_name FROM lounge ORDER BY lounge_id";
        return jdbcTemplate.query(sql, rs -> new PolicyOptionDto(
                rs.getInt("lounge_id"),
                rs.getString("lounge_name")
        ));
    }

    public List<PolicyPreviewDto> selectVipPolicyPreviews(List<Integer> branchIds, List<Integer> paymentIds) {
        String sql = "SELECT br.branch_id, br.branch_name, p.payment_id, p.payment_type, vp.vip_rate "
                + "FROM vip_policy vp "
                + "JOIN branch br ON vp.branch_id = br.branch_id "
                + "JOIN payment p ON vp.payment_id = p.payment_id "
                + "WHERE vp.branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND vp.payment_id IN (" + placeholders(paymentIds.size()) + ") "
                + "ORDER BY br.branch_id, p.payment_id";

        return jdbcTemplate.query(sql, pstmt -> {
            int parameterIndex = 1;
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            setIntValues(pstmt, parameterIndex, paymentIds);
        }, rs -> new PolicyPreviewDto(
                rs.getInt("branch_id"),
                rs.getString("branch_name"),
                null,
                null,
                rs.getInt("payment_id"),
                rs.getString("payment_type"),
                null,
                null,
                null,
                null,
                String.valueOf(rs.getDouble("vip_rate"))
        ));
    }

    public List<PolicyPreviewDto> selectMileagePolicyPreviews(List<Integer> branchIds, List<Integer> brandIds, List<Integer> paymentIds) {
        String sql = "SELECT br.branch_id, br.branch_name, ba.brand_id, ba.brand_name, p.payment_id, p.payment_type, mp.mileage_rate "
                + "FROM mileage_policy mp "
                + "JOIN branch br ON mp.branch_id = br.branch_id "
                + "JOIN brand ba ON mp.brand_id = ba.brand_id "
                + "JOIN payment p ON mp.payment_id = p.payment_id "
                + "WHERE mp.branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND mp.brand_id IN (" + placeholders(brandIds.size()) + ") "
                + "AND mp.payment_id IN (" + placeholders(paymentIds.size()) + ") "
                + "ORDER BY br.branch_id, ba.brand_id, p.payment_id";

        return jdbcTemplate.query(sql, pstmt -> {
            int parameterIndex = 1;
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            parameterIndex = setIntValues(pstmt, parameterIndex, brandIds);
            setIntValues(pstmt, parameterIndex, paymentIds);
        }, rs -> new PolicyPreviewDto(
                rs.getInt("branch_id"),
                rs.getString("branch_name"),
                rs.getInt("brand_id"),
                rs.getString("brand_name"),
                rs.getInt("payment_id"),
                rs.getString("payment_type"),
                null,
                null,
                null,
                null,
                String.valueOf(rs.getDouble("mileage_rate"))
        ));
    }

    public List<PolicyPreviewDto> selectValetPolicyPreviews(List<Integer> branchIds, List<Integer> membershipIds) {
        String sql = "SELECT br.branch_id, br.branch_name, m.membership_id, m.membership_grade, "
                + "vp.last_year_vip_min_standard, vp.last_year_vip_max_standard, vp.valet_available "
                + "FROM valet_policy vp "
                + "JOIN branch br ON vp.branch_id = br.branch_id "
                + "JOIN membership m ON vp.membership_id = m.membership_id "
                + "WHERE vp.branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND vp.membership_id IN (" + placeholders(membershipIds.size()) + ") "
                + "ORDER BY br.branch_id, m.membership_id";

        return jdbcTemplate.query(sql, pstmt -> {
            int parameterIndex = 1;
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            setIntValues(pstmt, parameterIndex, membershipIds);
        }, rs -> new PolicyPreviewDto(
                rs.getInt("branch_id"),
                rs.getString("branch_name"),
                null,
                null,
                null,
                null,
                rs.getInt("membership_id"),
                rs.getString("membership_grade"),
                null,
                null,
                toRangeValue(rs.getObject("last_year_vip_min_standard"), rs.getObject("last_year_vip_max_standard"))
                        + " / " + toAvailableText(rs.getInt("valet_available"))
        ));
    }

    public List<PolicyPreviewDto> selectFreeParkingPolicyPreviews(List<Integer> branchIds, List<Integer> membershipIds) {
        String sql = "SELECT br.branch_id, br.branch_name, m.membership_id, m.membership_grade, fp.free_parking_available "
                + "FROM free_parking_policy fp "
                + "JOIN branch br ON fp.branch_id = br.branch_id "
                + "JOIN membership m ON fp.membership_id = m.membership_id "
                + "WHERE fp.branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND fp.membership_id IN (" + placeholders(membershipIds.size()) + ") "
                + "ORDER BY br.branch_id, m.membership_id";

        return jdbcTemplate.query(sql, pstmt -> {
            int parameterIndex = 1;
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            setIntValues(pstmt, parameterIndex, membershipIds);
        }, rs -> new PolicyPreviewDto(
                rs.getInt("branch_id"),
                rs.getString("branch_name"),
                null,
                null,
                null,
                null,
                rs.getInt("membership_id"),
                rs.getString("membership_grade"),
                null,
                null,
                toAvailableText(rs.getInt("free_parking_available"))
        ));
    }

    public List<PolicyPreviewDto> selectLoungePolicyPreviews(List<Integer> branchIds, List<Integer> loungeIds, List<Integer> membershipIds) {
        String sql = "SELECT br.branch_id, br.branch_name, l.lounge_id, l.lounge_name, "
                + "m.membership_id, m.membership_grade, lp.lounge_available "
                + "FROM lounge_policy lp "
                + "JOIN branch br ON lp.branch_id = br.branch_id "
                + "JOIN lounge l ON lp.lounge_id = l.lounge_id "
                + "JOIN membership m ON lp.membership_id = m.membership_id "
                + "WHERE lp.branch_id IN (" + placeholders(branchIds.size()) + ") "
                + "AND lp.lounge_id IN (" + placeholders(loungeIds.size()) + ") "
                + "AND lp.membership_id IN (" + placeholders(membershipIds.size()) + ") "
                + "ORDER BY br.branch_id, l.lounge_id, m.membership_id";

        return jdbcTemplate.query(sql, pstmt -> {
            int parameterIndex = 1;
            parameterIndex = setIntValues(pstmt, parameterIndex, branchIds);
            parameterIndex = setIntValues(pstmt, parameterIndex, loungeIds);
            setIntValues(pstmt, parameterIndex, membershipIds);
        }, rs -> new PolicyPreviewDto(
                rs.getInt("branch_id"),
                rs.getString("branch_name"),
                null,
                null,
                null,
                null,
                rs.getInt("membership_id"),
                rs.getString("membership_grade"),
                rs.getInt("lounge_id"),
                rs.getString("lounge_name"),
                toAvailableText(rs.getInt("lounge_available"))
        ));
    }

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

    private String toAvailableText(int value) {
        return value == 1 ? "가능" : "불가";
    }

    private String toRangeValue(Object min, Object max) {
        String minText = min == null ? "미설정" : String.valueOf(min);
        String maxText = max == null ? "미설정" : String.valueOf(max);
        return minText + " ~ " + maxText;
    }
}
