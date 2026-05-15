package membership.service;

import membership.dao.MembershipDao;
import membership.dto.MembershipCurrentGradeDto;
import membership.dto.MembershipHistoryDto;

import java.time.LocalDate;
import java.util.List;

public class MembershipService {

    private final MembershipDao membershipDao;

    public MembershipService() {
        this.membershipDao = new MembershipDao();
    }

    public MembershipService(MembershipDao membershipDao) {
        this.membershipDao = membershipDao;
    }

    public List<MembershipCurrentGradeDto> getCurrentMembershipGrades() {
        return membershipDao.selectAllCurrentMembershipGrades();
    }

    public List<MembershipCurrentGradeDto> getEarlyGreenMembers() {
        return membershipDao.selectEarlyGreenMembers();
    }

    public List<MembershipHistoryDto> getMembershipHistories(String name, LocalDate birth) {
        validateNotBlank(name, "이름");

        if (birth == null) {
            throw new IllegalArgumentException("생년월일은 필수입니다.");
        }

        return membershipDao.selectMembershipHistoriesByNameAndBirth(name, birth);
    }

    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 필수입니다.");
        }
    }
}
