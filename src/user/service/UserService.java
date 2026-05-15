package user.service;

import membership.dto.MembershipDto;
import user.dao.UserDao;
import user.dto.UserDto;
import user.dto.UserTotalInfoDto;

import java.time.LocalDate;
import java.util.List;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<UserDto> getAllUsers() {
        return userDao.selectAllUsers();
    }

    public List<MembershipDto> getMemberships() {
        return userDao.selectAllMemberships();
    }

    public List<UserDto> getUsersByMembershipId(int membershipId) {
        validatePositive(membershipId, "멤버십 ID");
        return userDao.selectUsersByMembershipId(membershipId);
    }

    public UserTotalInfoDto getUserDetailByNameAndBirth(String name, LocalDate birth) {
        validateNotBlank(name, "이름");

        if (birth == null) {
            throw new IllegalArgumentException("생년월일을 입력해주세요.");
        }

        return userDao.selectUserDetailByNameAndBirth(name, birth);
    }

    public int registerUser(UserDto user) {
        validateUser(user, false);
        return userDao.insertUser(user);
    }

    public int updateUser(UserDto user) {
        validateUser(user, true);
        return userDao.updateUser(user);
    }

    private void validateUser(UserDto user, boolean requireUserId) {
        if (user == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        if (requireUserId) {
            validatePositive(user.getUserId(), "회원 ID");
        }

        validateNotBlank(user.getName(), "이름");
        validateNotBlank(user.getGender(), "성별");
        validateNotBlank(user.getPhoneNumber(), "전화번호");
        validateNotBlank(user.getCardNumber(), "카드번호");

        if (user.getGender().length() != 1) {
            throw new IllegalArgumentException("성별은 1글자로 입력해주세요.");
        }

        if (user.getBirth() == null) {
            throw new IllegalArgumentException("생년월일을 입력해주세요.");
        }

        if (user.getCardPeriod() == null) {
            throw new IllegalArgumentException("카드 유효기간을 입력해주세요.");
        }
    }

    private void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + "는 1 이상이어야 합니다.");
        }
    }

    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "을(를) 입력해주세요.");
        }
    }
}
