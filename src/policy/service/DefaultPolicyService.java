package policy.service;

import master.dto.request.BranchRegisterRequestDto;
import master.dto.request.BrandRegisterRequestDto;
import master.dto.request.LoungeRegisterRequestDto;
import master.dto.request.PaymentRegisterRequestDto;
import membership.dto.request.MembershipRegisterRequestDto;
import policy.dao.DefaultPolicyDao;

public class DefaultPolicyService {

    private final DefaultPolicyDao defaultPolicyDao = new DefaultPolicyDao();

    public boolean createDefaultPoliciesForNewBranch(int branchId, BranchRegisterRequestDto requestDto) {
        return defaultPolicyDao.createDefaultPoliciesForNewBranch(branchId, requestDto);
    }

    public boolean createDefaultPoliciesForNewBrand(int brandId, BrandRegisterRequestDto requestDto) {
        return defaultPolicyDao.createDefaultPoliciesForNewBrand(brandId, requestDto);
    }

    public boolean createDefaultPoliciesForNewPayment(int paymentId, PaymentRegisterRequestDto requestDto) {
        return defaultPolicyDao.createDefaultPoliciesForNewPayment(paymentId, requestDto);
    }

    public boolean createDefaultPoliciesForNewMembership(int membershipId, MembershipRegisterRequestDto requestDto) {
        return defaultPolicyDao.createDefaultPoliciesForNewMembership(membershipId, requestDto);
    }

    public boolean createDefaultPoliciesForNewLounge(int loungeId, LoungeRegisterRequestDto requestDto) {
        return defaultPolicyDao.createDefaultPoliciesForNewLounge(loungeId, requestDto);
    }
}
