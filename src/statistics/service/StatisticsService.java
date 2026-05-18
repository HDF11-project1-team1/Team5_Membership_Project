package statistics.service;

import statistics.dao.StatisticsDao;
import statistics.dto.MonthlyStatDto;
import statistics.dto.StatDto;

import java.util.List;

public class StatisticsService {

    private final StatisticsDao dao = new StatisticsDao();

    public List<StatDto> getStatByCategory(int y, int m) {
        return dao.selectStatByCategory(y, m);
    }

    public List<StatDto> getStatByAge(int y, int m) {
        return dao.selectStatByAge(y, m);
    }

    public List<StatDto> getStatByGender(int y, int m) {
        return dao.selectStatByGender(y, m);
    }

    public List<StatDto> getStatByMembership(int y, int m) {
        return dao.selectStatByMembership(y, m);
    }

    public List<StatDto> getStatByBranch(int y, int m) {
        return dao.selectStatByBranch(y, m);
    }

    public List<StatDto> getStatByBrand(int y, int m) {
        return dao.selectStatByBrand(y, m);
    }

    public List<StatDto> getStatByPayment(int y, int m) {
        return dao.selectStatByPayment(y, m);
    }

    public List<StatDto> getTopBuyers(int y, int m) {
        return dao.selectTopBuyers(y, m);
    }

    public List<StatDto> getBenefitUsageRate(int y, int m) {
        return dao.selectBenefitUsageRate(y, m);
    }

    public List<StatDto> getVipChangeByAgeGender(int y, int m) {
        return dao.selectVipChangeByAgeGender(y, m);
    }

    // 월별 추이 조회(Swing 차트용)
    public List<MonthlyStatDto> getMonthlyCategoryTrend(int year) {
        return dao.selectMonthlyCategoryTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyAgeTrend(int year) {
        return dao.selectMonthlyAgeTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyGenderTrend(int year) {
        return dao.selectMonthlyGenderTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyMembershipTrend(int year) {
        return dao.selectMonthlyMembershipTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyBranchTrend(int year) {
        return dao.selectMonthlyBranchTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyBrandTrend(int year) {
        return dao.selectMonthlyBrandTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyPaymentTrend(int year) {
        return dao.selectMonthlyPaymentTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyTopBuyersTrend(int year) {
        return dao.selectMonthlyTopBuyersTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyBenefitTrend(int year) {
        return dao.selectMonthlyBenefitTrend(year);
    }

    public List<MonthlyStatDto> getMonthlyVipTrend(int year) {
        return dao.selectMonthlyVipTrend(year);
    }
}
