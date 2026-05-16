package master.service;

import master.dao.BranchDao;
import master.dto.BranchDto;
import master.dto.request.BranchRegisterRequestDto;
import policy.service.DefaultPolicyService;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;
import static common.validation.InputValidator.isValidMileageRate;
import static common.validation.InputValidator.isValidRate;
import static common.validation.InputValidator.isValidStandardRange;

public class BranchService {

    private final BranchDao branchDao = new BranchDao();
    private final DefaultPolicyService defaultPolicyService = new DefaultPolicyService();

    public boolean registerBranch(String branchName, String branchAddress) {
        if (!hasText(branchName) || !hasText(branchAddress)) {
            return false;
        }
        if (branchDao.existsBranchName(branchName)) {
            return false;
        }

        BranchDto branchDto = new BranchDto(0, branchName, branchAddress);
        return branchDao.insertBranch(branchDto) > 0;
    }

    public boolean registerBranch(BranchRegisterRequestDto requestDto) {
        if (requestDto == null) {
            return false;
        }
        if (!hasText(requestDto.getBranchName()) || !hasText(requestDto.getBranchAddress())) {
            return false;
        }
        if (!isValidRate(requestDto.getDefaultVipRate()) || !isValidMileageRate(requestDto.getDefaultMileageRate())) {
            return false;
        }
        if (!isValidStandardRange(requestDto.getLastYearVipMinStandard(), requestDto.getLastYearVipMaxStandard())) {
            return false;
        }
        if (branchDao.existsBranchName(requestDto.getBranchName())) {
            return false;
        }

        BranchDto branchDto = new BranchDto(0, requestDto.getBranchName(), requestDto.getBranchAddress());
        int branchId = branchDao.insertBranchAndReturnId(branchDto);
        if (!isValidId(branchId)) {
            return false;
        }

        return defaultPolicyService.createDefaultPoliciesForNewBranch(branchId, requestDto);
    }

    public List<BranchDto> findBranchList() {
        return branchDao.selectAllBranches();
    }

    public BranchDto findBranchDetail(int branchId) {
        if (!isValidId(branchId)) {
            return null;
        }
        return branchDao.selectBranchById(branchId);
    }

    public boolean updateBranch(int branchId, String branchName, String branchAddress) {
        if (!isValidId(branchId) || !hasText(branchName) || !hasText(branchAddress)) {
            return false;
        }
        if (!branchDao.existsBranchId(branchId)) {
            return false;
        }

        BranchDto branchDto = new BranchDto(branchId, branchName, branchAddress);
        return branchDao.updateBranch(branchDto) > 0;
    }
}
