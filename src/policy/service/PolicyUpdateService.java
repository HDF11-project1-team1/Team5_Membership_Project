package policy.service;

import policy.dao.PolicyUpdateDao;

import java.util.List;

import static common.validation.InputValidator.isValidId;
import static common.validation.InputValidator.isValidRate;
import static common.validation.InputValidator.isValidStandardRange;

public class PolicyUpdateService {

    private final PolicyUpdateDao policyUpdateDao = new PolicyUpdateDao();

    public int updateVipRate(List<Integer> branchIds, List<Integer> paymentIds, double vipRate) {
        if (!isValidIdList(branchIds) || !isValidIdList(paymentIds) || !isValidRate(vipRate)) {
            return 0;
        }

        return policyUpdateDao.updateVipRate(branchIds, paymentIds, vipRate);
    }

    public int updateMileageRate(List<Integer> branchIds, List<Integer> brandIds, List<Integer> paymentIds, double mileageRate) {
        if (!isValidIdList(branchIds)
                || !isValidIdList(brandIds)
                || !isValidIdList(paymentIds)
                || !isValidRate(mileageRate)) {
            return 0;
        }

        return policyUpdateDao.updateMileageRate(branchIds, brandIds, paymentIds, mileageRate);
    }

    public int updateFreeParkingAvailable(List<Integer> branchIds, List<Integer> membershipIds, boolean available) {
        if (!isValidIdList(branchIds) || !isValidIdList(membershipIds)) {
            return 0;
        }

        return policyUpdateDao.updateFreeParkingAvailable(branchIds, membershipIds, available);
    }

    public int updateLoungeAvailable(List<Integer> branchIds, List<Integer> loungeIds, List<Integer> membershipIds, boolean available) {
        if (!isValidIdList(branchIds) || !isValidIdList(loungeIds) || !isValidIdList(membershipIds)) {
            return 0;
        }

        return policyUpdateDao.updateLoungeAvailable(branchIds, loungeIds, membershipIds, available);
    }

    public int updateValetPolicy(List<Integer> branchIds, List<Integer> membershipIds, int minStandard, int maxStandard, boolean available) {
        if (!isValidIdList(branchIds) || !isValidIdList(membershipIds) || !isValidStandardRange(minStandard, maxStandard)) {
            return 0;
        }

        return policyUpdateDao.updateValetPolicy(branchIds, membershipIds, minStandard, maxStandard, available);
    }

    private boolean isValidIdList(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Integer id : ids) {
            if (id == null || !isValidId(id)) {
                return false;
            }
        }
        return true;
    }
}
