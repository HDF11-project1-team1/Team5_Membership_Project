package membership.service;

import membership.dao.MembershipDao;
import membership.dto.MembershipDto;
import membership.dto.request.MembershipRegisterRequestDto;
import policy.service.DefaultPolicyService;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidAmount;
import static common.validation.InputValidator.isValidId;
import static common.validation.InputValidator.isValidRate;
import static common.validation.InputValidator.isValidStandardRange;

public class MembershipService {

    private final MembershipDao membershipDao = new MembershipDao();
    private final DefaultPolicyService defaultPolicyService = new DefaultPolicyService();

    public boolean registerMembership(MembershipRegisterRequestDto requestDto) {
        if (requestDto == null) {
            return false;
        }
        if (!hasText(requestDto.getMembershipGrade())) {
            return false;
        }
        if (!isValidAmount(requestDto.getMinAmount())
                || !isValidRate(requestDto.getMaxDiscount())
                || !isValidRate(requestDto.getBasicDiscount())
                || !isValidAmount(requestDto.getSpecialDiscountAmount())
                || !isValidAmount(requestDto.getCoffeeCount())
                || !isValidAmount(requestDto.getRewardOfferStandard())
                || !isValidAmount(requestDto.getRewardAmount())) {
            return false;
        }
        if (!isValidStandardRange(requestDto.getLastYearVipMinStandard(), requestDto.getLastYearVipMaxStandard())) {
            return false;
        }

        MembershipDto membershipDto = new MembershipDto(
                0,
                requestDto.getMembershipGrade(),
                requestDto.getMinAmount(),
                requestDto.getMaxDiscount(),
                requestDto.getBasicDiscount(),
                requestDto.getSpecialDiscountAmount(),
                requestDto.getCoffeeCount()
        );
        int membershipId = membershipDao.insertMembershipAndReturnId(membershipDto);
        if (!isValidId(membershipId)) {
            return false;
        }

        return defaultPolicyService.createDefaultPoliciesForNewMembership(membershipId, requestDto);
    }

    public List<MembershipDto> findMembershipList() {
        return membershipDao.selectAllMemberships();
    }
}
