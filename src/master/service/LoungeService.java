package master.service;

import master.dao.LoungeDao;
import master.dto.LoungeDto;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class LoungeService {

    private final LoungeDao loungeDao = new LoungeDao();

    // ===== ?쇱슫吏 ?깅줉 =====
    public boolean registerLounge(String loungeName) {
        if (!hasText(loungeName)) {
            return false;
        }
        if (loungeDao.existsLoungeName(loungeName)) {
            return false;
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
            return null;
        }
        return loungeDao.selectLoungeById(loungeId);
    }

    // ===== ?쇱슫吏 ?섏젙 =====
    public boolean updateLounge(int loungeId, String loungeName) {
        if (!isValidId(loungeId) || !hasText(loungeName)) {
            return false;
        }
        if (!loungeDao.existsLoungeId(loungeId)) {
            return false;
        }

        LoungeDto loungeDto = new LoungeDto(loungeId, loungeName);
        return loungeDao.updateLounge(loungeDto) > 0;
    }

}

