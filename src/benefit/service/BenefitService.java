package benefit.service;

import benefit.dao.BenefitDao;
import benefit.dto.GreenVehicleBranchDto;
import benefit.dto.ParkingHistoryDto;
import benefit.dto.RewardHistoryDto;
import benefit.dto.UserInfoDto;
import benefit.dto.VehicleDto;
import common.connection.DBConnection;
import common.connection.DBType;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Collections;

public class BenefitService {

    private final BenefitDao benefitDao = new BenefitDao();

    // ═══════════════════════════════════════════════════
    // 1. 라운지 이용가능 여부 확인
    // ═══════════════════════════════════════════════════

    public boolean checkLoungePolicyAvailability(String membershipGrade, String branchName, String loungeName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return benefitDao.getLoungePolicyAvailable(conn, membershipGrade, branchName, loungeName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 2. Cafe-H 커피 지급수 조회
    // ═══════════════════════════════════════════════════

    public int checkCafeHPolicyCount(String membershipGrade) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return benefitDao.getCafeHPolicyCount(conn, membershipGrade);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 3. 차량 등록 / 변경
    // ═══════════════════════════════════════════════════

    /** 차량 등록 (BASIC 등급 불가, 등급별 최대 대수 제한) */
    public String registerVehicle(String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[차량 등록 실패] 해당 이름(" + name + ")의 회원을 찾을 수 없습니다.";
            }
            int userId = userInfo.getUserId();
            String grade = userInfo.getMembershipGrade();

            if (grade.equalsIgnoreCase("BASIC")) {
                return "[차량 등록 실패] BASIC 등급은 차량을 등록할 수 없습니다.";
            }

            int currentCount = benefitDao.getVehicleCount(conn, userId);
            boolean isJasminTop = grade.equalsIgnoreCase("JASMIN SIGNATURE") || grade.equalsIgnoreCase("JASMIN BLACK");
            int maxLimit = isJasminTop ? 2 : 1;

            if (currentCount >= maxLimit) {
                return "[차량 등록 실패] 현재 " + currentCount + "대 등록됨. (최대 " + maxLimit + "대)";
            }

            VehicleDto vehicleDto = new VehicleDto();
            vehicleDto.setUserId(userId);
            vehicleDto.setCarNumber(carNumber);
            benefitDao.insertVehicle(conn, vehicleDto);

            conn.commit();
            return "[차량 등록 성공] 차량번호: " + carNumber;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            return "[차량 등록 실패] " + e.getMessage();
        } finally {
            DBConnection.close(conn);
        }
    }

    /** 차량 번호 변경 (월 1회 제한) */
    public String modifyVehicle(String name, String oldCarNumber, String newCarNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[차량 변경 실패] 해당 이름(" + name + ")의 회원을 찾을 수 없습니다.";
            }
            int userId = userInfo.getUserId();

            java.sql.Date regDate = benefitDao.getVehicleRegisteredDate(conn, userId, oldCarNumber);
            if (regDate == null) {
                return "[차량 변경 실패] 등록된 차량(" + oldCarNumber + ")을 찾을 수 없습니다.";
            }

            Calendar regCal = Calendar.getInstance();
            regCal.setTime(regDate);
            Calendar nowCal = Calendar.getInstance();

            boolean sameMonth = regCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                    && regCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH);
            if (sameMonth) {
                return "[차량 변경 실패] 이번 달에 이미 변경하셨습니다. 다음 달에 변경 가능합니다.";
            }

            VehicleDto newVehicle = new VehicleDto();
            newVehicle.setUserId(userId);
            newVehicle.setCarNumber(newCarNumber);
            int updated = benefitDao.updateVehicle(conn, newVehicle, oldCarNumber);
            if (updated == 0) {
                return "[차량 변경 실패] 업데이트된 차량이 없습니다.";
            }

            conn.commit();
            return "[차량 변경 성공] " + oldCarNumber + " → " + newCarNumber;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            return "[차량 변경 실패] " + e.getMessage();
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 4. 무료주차 가능 여부 확인
    // ═══════════════════════════════════════════════════

    /** 무료주차 판별 (GREEN 등급은 지점 등록 확인 후, 전 등급 1일 1회·3시간 제한) */
    public String checkFreeParkingAvailability(String branchName, String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);

            UserInfoDto userInfo = benefitDao.getUserAndVehicleInfo(conn, name, carNumber);
            if (userInfo == null) {
                return "[무료주차] 해당 회원의 등록 차량과 입력 차량번호가 일치하지 않거나 회원을 찾을 수 없습니다.";
            }

            boolean hasPolicy = benefitDao.getFreeParkingPolicy(conn, userInfo.getMembershipId(), branchName);
            if (!hasPolicy) {
                return "[무료주차] 해당 지점/멤버십 등급은 무료주차 정책이 없습니다.";
            }

            // GREEN 2 / EARLY GREEN: 등록 지점 확인 (미등록 시 차단)
            String grade = userInfo.getMembershipGrade();
            if (grade.equalsIgnoreCase("GREEN 2") || grade.equalsIgnoreCase("EARLY GREEN")) {
                if (!benefitDao.hasGreenBranchByBranchName(conn, userInfo.getUserId(), branchName)) {
                    return "[무료주차] GREEN 2 / EARLY GREEN: 해당 지점에 그린 차량이 등록되어 있지 않습니다.";
                }
            }

            // 오늘 주차 이력 확인 (1일 1회, 3시간 제한)
            ParkingHistoryDto todayHistory = benefitDao.getTodayParkingHistory(conn, userInfo.getVehicleId());
            if (todayHistory != null) {
                if (todayHistory.getExitDate() == null) {
                    long minutes = ChronoUnit.MINUTES.between(todayHistory.getEntryDate(), LocalDateTime.now());
                    if (minutes <= 180) {
                        return "[무료주차] 현재 주차 중. 입차 후 " + minutes + "분 경과 (3시간 이내 무료)";
                    } else {
                        return "[무료주차] 3시간 초과. 무료주차 불가.";
                    }
                } else {
                    return "[무료주차] 오늘 이미 무료주차를 사용했습니다. (1일 1회 제한)";
                }
            }

            return "[무료주차] 무료 주차 가능";

        } catch (Exception e) {
            e.printStackTrace();
            return "[무료주차] 오류 발생: " + e.getMessage();
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 5. 발레파킹 가능 여부 확인
    // ═══════════════════════════════════════════════════

    /** 발레파킹 판별 (작년 산정적립금 → 정책 확인 → 차량 소유 → 오늘 중복 사용 여부) */
    public String checkValetParkingAvailability(String branchName, String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[발레파킹] 해당 이름의 회원을 찾을 수 없습니다.";
            }

            int calculatedAmount = benefitDao.getLastYearCalculatedAmount(conn, userInfo.getUserId());
            if (calculatedAmount == -1) {
                return "[발레파킹] 작년 산정적립금 이력이 존재하지 않습니다. 발레파킹 조건 미달.";
            }

            boolean policyOk = benefitDao.isValetPolicyAvailable(conn, userInfo.getMembershipId(), branchName,
                    calculatedAmount);
            if (!policyOk) {
                return "[발레파킹] 해당 멤버십/지점/적립금 조건으로 발레파킹이 불가합니다.";
            }

            int vehicleId = benefitDao.getVehicleIdByCarNumber(conn, userInfo.getUserId(), carNumber);
            if (vehicleId == -1) {
                return "[발레파킹] 입력한 차량번호가 해당 회원의 등록차량과 일치하지 않습니다.";
            }

            if (benefitDao.hasValetHistoryToday(conn, vehicleId)) {
                return "[발레파킹] 오늘 이미 발레파킹 서비스를 사용했습니다.";
            }

            return "[발레파킹] 발레 파킹 가능";

        } catch (Exception e) {
            e.printStackTrace();
            return "[발레파킹] 오류 발생: " + e.getMessage();
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 6. 특별할인 잔액 조회
    // ═══════════════════════════════════════════════════

    public int checkSpecialDiscountBalance(String name) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return benefitDao.getSpecialDiscountBalance(conn, name);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 7. 그린 차량 지점 등록
    // ═══════════════════════════════════════════════════

    /** 그린 지점 등록/변경 (미등록 시 최초 등록, 최대 2회 변경 제한) */
    public String modifyGreenBranch(String name, String newBranchName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[지점 변경 실패] 해당 이름(" + name + ")의 회원을 찾을 수 없습니다.";
            }
            int userId = userInfo.getUserId();

            int newBranchId = benefitDao.getBranchIdByName(conn, newBranchName);
            if (newBranchId == -1) {
                return "[지점 변경 실패] 입력하신 지점명(" + newBranchName + ")을 찾을 수 없습니다.";
            }

            int modifiedCount = benefitDao.getGreenBranchModifiedCount(conn, userId);

            GreenVehicleBranchDto branchDto = new GreenVehicleBranchDto();
            branchDto.setUserId(userId);
            branchDto.setBranchId(newBranchId);

            if (modifiedCount == -1) {
                benefitDao.insertGreenBranch(conn, branchDto);
                conn.commit();
                return "[지점 등록 성공] " + newBranchName + " 지점으로 최초 등록 완료되었습니다.";
            }

            if (modifiedCount >= 2) {
                return "[지점 변경 실패] 지점은 최대 2회까지만 변경 가능합니다. (현재 " + modifiedCount + "회 사용)";
            }

            benefitDao.updateGreenBranch(conn, branchDto);
            conn.commit();
            return "[지점 변경 성공] 변경 횟수: " + (modifiedCount + 1) + "/2회 사용";

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            return "[지점 변경 실패] " + e.getMessage();
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 8. 리워드 지급 이력 조회
    // ═══════════════════════════════════════════════════

    public List<RewardHistoryDto> checkRewardHistory(String name) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return benefitDao.getRewardHistoryByName(conn, name);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            DBConnection.close(conn);
        }
    }
}
