package master.service;

import master.dao.LoungeDao;
import master.dto.LoungeDto;
import master.dto.request.LoungeRegisterRequestDto;
import policy.service.DefaultPolicyService;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class LoungeService {

    private final LoungeDao loungeDao = new LoungeDao();
    private final DefaultPolicyService defaultPolicyService = new DefaultPolicyService();

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

    public boolean registerLounge(LoungeRegisterRequestDto requestDto) {
        if (requestDto == null) {
            return false;
        }
        if (!hasText(requestDto.getLoungeName())) {
            return false;
        }
        if (loungeDao.existsLoungeName(requestDto.getLoungeName())) {
            return false;
        }

        LoungeDto loungeDto = new LoungeDto(0, requestDto.getLoungeName());
        int loungeId = loungeDao.insertLoungeAndReturnId(loungeDto);
        if (!isValidId(loungeId)) {
            return false;
        }

        return defaultPolicyService.createDefaultPoliciesForNewLounge(loungeId, requestDto);
    }

    public List<LoungeDto> findLoungeList() {
        return loungeDao.selectAllLounges();
    }

    public LoungeDto findLoungeDetail(int loungeId) {
        if (!isValidId(loungeId)) {
            return null;
        }
        return loungeDao.selectLoungeById(loungeId);
    }

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
