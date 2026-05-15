package benefit.service;

import benefit.dao.BenefitDao;
import benefit.dto.RewardHistoryDto;

import java.util.List;

public class BenefitService {

    private final BenefitDao benefitDao = new BenefitDao();

    public boolean getLoungePolicyAvailability(String membershipGrade, String branchName, String loungeName) {
        return benefitDao.selectLoungePolicyAvailability(membershipGrade, branchName, loungeName);
    }

    public int getCafeHPolicyCount(String membershipGrade) {
        return benefitDao.selectCafeHPolicyCount(membershipGrade);
    }

    public String registerVehicle(String name, String carNumber) {
        return benefitDao.insertVehicleByUserName(name, carNumber);
    }

    public String updateVehicle(String name, String oldCarNumber, String newCarNumber) {
        return benefitDao.updateVehicleByUserName(name, oldCarNumber, newCarNumber);
    }

    public String getFreeParkingAvailability(String branchName, String name, String carNumber) {
        return benefitDao.selectFreeParkingAvailability(branchName, name, carNumber);
    }

    public String getValetParkingAvailability(String branchName, String name, String carNumber) {
        return benefitDao.selectValetParkingAvailability(branchName, name, carNumber);
    }

    public int getSpecialDiscountBalance(String name) {
        return benefitDao.selectSpecialDiscountBalance(name);
    }

    public String updateGreenBranch(String name, String newBranchName) {
        return benefitDao.updateGreenBranchByUserName(name, newBranchName);
    }

    public List<RewardHistoryDto> getRewardHistory(String name) {
        return benefitDao.selectRewardHistory(name);
    }
}

