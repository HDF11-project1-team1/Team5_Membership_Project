package master.service;

import common.exception.DuplicateException;
import common.exception.NotFoundException;
import common.exception.ValidationException;
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

    // ===== 지점 등록 =====
    public boolean registerBranch(String branchName, String branchAddress) {
        if (!hasText(branchName) || !hasText(branchAddress)) {
            throw new ValidationException("지점명과 지점 주소는 필수입니다.");
        }
        if (branchDao.existsBranchName(branchName)) {
            throw new DuplicateException("이미 등록된 지점명입니다.");
        }

        BranchDto branchDto = new BranchDto(0, branchName, branchAddress);
        int branchId = branchDao.insertBranchAndReturnId(branchDto);
        if (branchId <= 0) {
            return false;
        }

        BranchRegisterRequestDto requestDto = new BranchRegisterRequestDto();
        requestDto.setBranchName(branchName);
        requestDto.setBranchAddress(branchAddress);

        return defaultPolicyService.createDefaultPoliciesForNewBranch(branchId, requestDto);
    }

    // ===== 전체 지점 목록 조회 =====
    public List<BranchDto> getBranchList() {
        return branchDao.selectAllBranches();
    }

    // ===== 특정 지점 상세 조회 =====
    public BranchDto getBranchDetail(int branchId) {
        if (!isValidId(branchId)) {
            throw new ValidationException("지점 ID는 1 이상이어야 합니다.");
        }
        BranchDto branch = branchDao.selectBranchById(branchId);
        if (branch == null) {
            throw new NotFoundException("지점을 찾을 수 없습니다.");
        }
        return branch;
    }

    public boolean updateBranch(int branchId, String branchName, String branchAddress) {
        if (!isValidId(branchId) || !hasText(branchName) || !hasText(branchAddress)) {
            throw new ValidationException("지점 ID, 지점명, 지점 주소는 필수입니다.");
        }
        if (!branchDao.existsBranchId(branchId)) {
            throw new NotFoundException("수정할 지점을 찾을 수 없습니다.");
        }

        BranchDto branchDto = new BranchDto(branchId, branchName, branchAddress);
        return branchDao.updateBranch(branchDto) > 0;
    }
}
