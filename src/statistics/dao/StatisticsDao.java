package statistics.dao;

import common.connection.DBConnection;
import common.connection.DBType;
import statistics.dto.MonthlyStatDto;
import statistics.dto.StatDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDao {

    // ── 단건 쿼리 헬퍼 (year, month 2개 파라미터) ─────────────────────
    private List<StatDto> executeQuery(String sql, int year, int month) {
        List<StatDto> list = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, year); pstmt.setInt(2, month);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(new StatDto(rs.getString(1), rs.getLong(2), rs.getLong(3)));
        } catch (SQLException e) { System.out.println("StatisticsDao.executeQuery: " + e.getMessage()); }
        finally { DBConnection.close(rs); DBConnection.close(pstmt); DBConnection.close(conn); }
        return list;
    }

    // ── 혜택 쿼리 헬퍼 (UNION ALL 4개 × 2 = 8 파라미터) ──────────────
    private List<StatDto> executeBenefitQuery(String sql, int year, int month) {
        List<StatDto> list = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= 8; i += 2) { pstmt.setInt(i, year); pstmt.setInt(i + 1, month); }
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(new StatDto(rs.getString(1), rs.getLong(2), rs.getLong(3)));
        } catch (SQLException e) { System.out.println("StatisticsDao.executeBenefitQuery: " + e.getMessage()); }
        finally { DBConnection.close(rs); DBConnection.close(pstmt); DBConnection.close(conn); }
        return list;
    }

    // ── 월별 추이 헬퍼 (year 1개 파라미터, GROUP BY month) ────────────
    private List<MonthlyStatDto> executeMonthlyQuery(String sql, int year) {
        List<MonthlyStatDto> list = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, year);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(new MonthlyStatDto(rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getLong(4)));
        } catch (SQLException e) { System.out.println("StatisticsDao.executeMonthlyQuery: " + e.getMessage()); }
        finally { DBConnection.close(rs); DBConnection.close(pstmt); DBConnection.close(conn); }
        return list;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  단월 조회 (기존) — year + month 필터
    // ═══════════════════════════════════════════════════════════════════
    private static final String YEAR_MONTH_FILTER =
            "EXTRACT(YEAR FROM ph.generated_date) = ? AND EXTRACT(MONTH FROM ph.generated_date) = ?";

    public List<StatDto> selectStatByCategory(int y, int m) {
        return executeQuery("SELECT c.category_name, COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN category c ON ph.category_id=c.category_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY c.category_name ORDER BY COUNT(*) DESC", y, m);
    }
    public List<StatDto> selectStatByAge(int y, int m) {
        return executeQuery("SELECT FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10||'대', COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10 ORDER BY 1", y, m);
    }
    public List<StatDto> selectStatByGender(int y, int m) {
        return executeQuery("SELECT u.gender, COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY u.gender ORDER BY 1", y, m);
    }
    public List<StatDto> selectStatByMembership(int y, int m) {
        return executeQuery("SELECT m.membership_grade, COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN membership m ON ph.membership_id=m.membership_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY m.membership_grade ORDER BY COUNT(*) DESC", y, m);
    }
    public List<StatDto> selectStatByBranch(int y, int m) {
        return executeQuery("SELECT b.branch_name, COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN branch b ON ph.branch_id=b.branch_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY b.branch_name ORDER BY COUNT(*) DESC", y, m);
    }
    public List<StatDto> selectStatByBrand(int y, int m) {
        return executeQuery("SELECT br.brand_name, COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN brand br ON ph.brand_id=br.brand_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY br.brand_name ORDER BY COUNT(*) DESC", y, m);
    }
    public List<StatDto> selectStatByPayment(int y, int m) {
        return executeQuery("SELECT p.payment_type, COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN payment p ON ph.payment_id=p.payment_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY p.payment_type ORDER BY COUNT(*) DESC", y, m);
    }
    public List<StatDto> selectTopBuyers(int y, int m) {
        return executeQuery("SELECT u.name||' (ID:'||u.user_id||')', COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY u.user_id,u.name ORDER BY SUM(ph.final_price) DESC FETCH FIRST 10 ROWS ONLY", y, m);
    }
    public List<StatDto> selectBenefitUsageRate(int y, int m) {
        String sql = "SELECT '전체 구매 건수', COUNT(*), SUM(final_price) FROM purchase_history WHERE EXTRACT(YEAR FROM generated_date)=? AND EXTRACT(MONTH FROM generated_date)=? " +
                "UNION ALL SELECT 'VIP 적립금 발생', COUNT(*), SUM(final_price) FROM purchase_history WHERE EXTRACT(YEAR FROM generated_date)=? AND EXTRACT(MONTH FROM generated_date)=? AND vip_amount>0 " +
                "UNION ALL SELECT '마일리지 적립', COUNT(*), SUM(final_price) FROM purchase_history WHERE EXTRACT(YEAR FROM generated_date)=? AND EXTRACT(MONTH FROM generated_date)=? AND mileage_amount>0 " +
                "UNION ALL SELECT '할인 적용 건수', COUNT(*), SUM(final_price) FROM purchase_history WHERE EXTRACT(YEAR FROM generated_date)=? AND EXTRACT(MONTH FROM generated_date)=? AND discount_price>0";
        return executeBenefitQuery(sql, y, m);
    }
    public List<StatDto> selectVipChangeByAgeGender(int y, int m) {
        return executeQuery("SELECT FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10||'대 '||u.gender, COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_MONTH_FILTER + " GROUP BY FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10, u.gender ORDER BY 1", y, m);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  월별 추이 조회 (Swing 차트용) — year만 필터, GROUP BY month
    // ═══════════════════════════════════════════════════════════════════
    private static final String YEAR_FILTER = "EXTRACT(YEAR FROM ph.generated_date) = ?";

    public List<MonthlyStatDto> selectMonthlyCategoryTrend(int year) {
        return executeMonthlyQuery("SELECT c.category_name, EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN category c ON ph.category_id=c.category_id WHERE " + YEAR_FILTER + " GROUP BY c.category_name, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
    public List<MonthlyStatDto> selectMonthlyAgeTrend(int year) {
        return executeMonthlyQuery("SELECT FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10||'대', EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_FILTER + " GROUP BY FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
    public List<MonthlyStatDto> selectMonthlyGenderTrend(int year) {
        return executeMonthlyQuery("SELECT u.gender, EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_FILTER + " GROUP BY u.gender, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
    public List<MonthlyStatDto> selectMonthlyMembershipTrend(int year) {
        return executeMonthlyQuery("SELECT m.membership_grade, EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN membership m ON ph.membership_id=m.membership_id WHERE " + YEAR_FILTER + " GROUP BY m.membership_grade, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
    public List<MonthlyStatDto> selectMonthlyBranchTrend(int year) {
        return executeMonthlyQuery("SELECT b.branch_name, EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN branch b ON ph.branch_id=b.branch_id WHERE " + YEAR_FILTER + " GROUP BY b.branch_name, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
    public List<MonthlyStatDto> selectMonthlyBrandTrend(int year) {
        return executeMonthlyQuery("SELECT br.brand_name, EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN brand br ON ph.brand_id=br.brand_id WHERE " + YEAR_FILTER + " GROUP BY br.brand_name, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
    public List<MonthlyStatDto> selectMonthlyPaymentTrend(int year) {
        return executeMonthlyQuery("SELECT p.payment_type, EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN payment p ON ph.payment_id=p.payment_id WHERE " + YEAR_FILTER + " GROUP BY p.payment_type, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
    public List<MonthlyStatDto> selectMonthlyTopBuyersTrend(int year) {
        return executeMonthlyQuery("SELECT u.name||' (ID:'||u.user_id||')', EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_FILTER + " GROUP BY u.user_id, u.name, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2, SUM(ph.final_price) DESC", year);
    }
    public List<MonthlyStatDto> selectMonthlyBenefitTrend(int year) {
        return executeMonthlyQuery("SELECT '전체 구매', EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph WHERE " + YEAR_FILTER + " GROUP BY EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2", year);
    }
    public List<MonthlyStatDto> selectMonthlyVipTrend(int year) {
        return executeMonthlyQuery("SELECT FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10||'대 '||u.gender, EXTRACT(MONTH FROM ph.generated_date), COUNT(*), SUM(ph.final_price) FROM purchase_history ph JOIN users u ON ph.user_id=u.user_id WHERE " + YEAR_FILTER + " GROUP BY FLOOR((EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM u.birth))/10)*10, u.gender, EXTRACT(MONTH FROM ph.generated_date) ORDER BY 2,1", year);
    }
}
