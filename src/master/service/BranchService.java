package master.service;

import master.dao.BranchDao;
import master.dto.BranchDto;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class BranchService {

    private final BranchDao branchDao = new BranchDao();

    // ===== 지점 등록 =====
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

    // ===== 전체 지점 목록 조회 =====
    public List<BranchDto> getBranchList() {
        return branchDao.selectAllBranches();
    }

    // ===== 특정 지점 상세 조회 =====
    public BranchDto getBranchDetail(int branchId) {
        if (!isValidId(branchId)) {
            return null;
        }
        return branchDao.selectBranchById(branchId);
    }

    // ===== 지점 정보 수정 =====
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

