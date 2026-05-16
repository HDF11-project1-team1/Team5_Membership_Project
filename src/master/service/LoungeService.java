package master.service;

import common.exception.DuplicateException;
import common.exception.NotFoundException;
import common.exception.ValidationException;
import master.dao.LoungeDao;
import master.dto.LoungeDto;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class LoungeService {

    private final LoungeDao loungeDao = new LoungeDao();

    // ===== 라운지 등록 =====
    public boolean registerLounge(String loungeName) {
        if (!hasText(loungeName)) {
            throw new ValidationException("라운지명은 필수입니다.");
        }
        if (loungeDao.existsLoungeName(loungeName)) {
            throw new DuplicateException("이미 등록된 라운지명입니다.");
        }

        LoungeDto loungeDto = new LoungeDto(0, loungeName);
        return loungeDao.insertLounge(loungeDto) > 0;
    }

    // ===== 라운지 목록 조회 =====
    public List<LoungeDto> getLoungeList() {
        return loungeDao.selectAllLounges();
    }

    // ===== 라운지 상세 조회 =====
    public LoungeDto getLoungeDetail(int loungeId) {
        if (!isValidId(loungeId)) {
            throw new ValidationException("라운지 ID는 1 이상이어야 합니다.");
        }
        LoungeDto lounge = loungeDao.selectLoungeById(loungeId);
        if (lounge == null) {
            throw new NotFoundException("라운지를 찾을 수 없습니다.");
        }
        return lounge;
    }

    // ===== 라운지 수정 =====
    public boolean updateLounge(int loungeId, String loungeName) {
        if (!isValidId(loungeId) || !hasText(loungeName)) {
            throw new ValidationException("라운지 ID와 라운지명은 필수입니다.");
        }
        if (!loungeDao.existsLoungeId(loungeId)) {
            throw new NotFoundException("수정할 라운지를 찾을 수 없습니다.");
        }

        LoungeDto loungeDto = new LoungeDto(loungeId, loungeName);
        return loungeDao.updateLounge(loungeDto) > 0;
    }
}
