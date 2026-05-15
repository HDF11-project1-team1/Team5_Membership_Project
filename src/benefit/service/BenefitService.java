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

public class BenefitService {

    private final BenefitDao benefitDao = new BenefitDao();

    // ═══════════════════════════════════════════════════
    // 1. 라운지 이용가능 여부 확인
    // ═══════════════════════════════════════════════════

    public boolean checkLoungePolicyAvailability(String membershipGrade, String branchName, String loungeName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            boolean available = benefitDao.getLoungePolicyAvailable(conn, membershipGrade, branchName, loungeName);
            if (available) {
                System.out.println(
                        "[라운지] " + membershipGrade + " 등급은 " + branchName + " 지점의 " + loungeName + "를 이용 가능합니다.");
            } else {
                System.out.println(
                        "[라운지] " + membershipGrade + " 등급은 " + branchName + " 지점의 " + loungeName + "를 이용할 수 없습니다.");
            }
            return available;
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
            int count = benefitDao.getCafeHPolicyCount(conn, membershipGrade);
            if (count > 0) {
                System.out.println("[Cafe-H] " + membershipGrade + " 등급은 월 " + count + "회 이용 가능합니다.");
            } else {
                System.out.println("[Cafe-H] " + membershipGrade + " 등급은 혜택이 없습니다.");
            }
            return count;
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
    public void registerVehicle(String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                System.out.println("[차량 등록 실패] 해당 이름(" + name + ")의 회원을 찾을 수 없습니다.");
                return;
            }
            int userId = userInfo.getUserId();
            String grade = userInfo.getMembershipGrade();

            if (grade.equalsIgnoreCase("BASIC")) {
                System.out.println("[차량 등록 실패] BASIC 등급은 차량을 등록할 수 없습니다.");
                return;
            }

            int currentCount = benefitDao.getVehicleCount(conn, userId);
            boolean isJasminTop = grade.equalsIgnoreCase("JASMIN SIGNATURE") || grade.equalsIgnoreCase("JASMIN BLACK");
            int maxLimit = isJasminTop ? 2 : 1;

            if (currentCount >= maxLimit) {
                System.out.println("[차량 등록 실패] 현재 " + currentCount + "대 등록됨. (최대 " + maxLimit + "대)");
                return;
            }

            VehicleDto vehicleDto = new VehicleDto();
            vehicleDto.setUserId(userId);
            vehicleDto.setCarNumber(carNumber);
            benefitDao.insertVehicle(conn, vehicleDto);

            conn.commit();
            System.out.println("[차량 등록 성공] 차량번호: " + carNumber);

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            System.out.println("[차량 등록 실패] " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(conn);
        }
    }

    /** 차량 번호 변경 (월 1회 제한) */
    public void modifyVehicle(String name, String oldCarNumber, String newCarNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                System.out.println("[차량 변경 실패] 해당 이름(" + name + ")의 회원을 찾을 수 없습니다.");
                return;
            }
            int userId = userInfo.getUserId();

            java.sql.Date regDate = benefitDao.getVehicleRegisteredDate(conn, userId, oldCarNumber);
            if (regDate == null) {
                System.out.println("[차량 변경 실패] 등록된 차량(" + oldCarNumber + ")을 찾을 수 없습니다.");
                return;
            }

            Calendar regCal = Calendar.getInstance();
            regCal.setTime(regDate);
            Calendar nowCal = Calendar.getInstance();

            boolean sameMonth = regCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                    && regCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH);
            if (sameMonth) {
                System.out.println("[차량 변경 실패] 이번 달에 이미 변경하셨습니다. 다음 달에 변경 가능합니다.");
                return;
            }

            VehicleDto newVehicle = new VehicleDto();
            newVehicle.setUserId(userId);
            newVehicle.setCarNumber(newCarNumber);
            int updated = benefitDao.updateVehicle(conn, newVehicle, oldCarNumber);
            if (updated == 0) {
                System.out.println("[차량 변경 실패] 업데이트된 차량이 없습니다.");
                return;
            }

            conn.commit();
            System.out.println("[차량 변경 성공] " + oldCarNumber + " → " + newCarNumber);

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            System.out.println("[차량 변경 실패] " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 4. 무료주차 가능 여부 확인
    // ═══════════════════════════════════════════════════

    /** 무료주차 판별 (GREEN 등급은 지점 등록 확인 후, 전 등급 1일 1회·3시간 제한) */
    public boolean checkFreeParkingAvailability(String branchName, String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);

            UserInfoDto userInfo = benefitDao.getUserAndVehicleInfo(conn, name, carNumber);
            if (userInfo == null) {
                System.out.println("[무료주차] 해당 회원의 등록 차량과 입력 차량번호가 일치하지 않거나 회원을 찾을 수 없습니다.");
                return false;
            }

            boolean hasPolicy = benefitDao.getFreeParkingPolicy(conn, userInfo.getMembershipId(), branchName);
            if (!hasPolicy) {
                System.out.println("[무료주차] 해당 지점/멤버십 등급은 무료주차 정책이 없습니다.");
                return false;
            }

            // GREEN 2 / EARLY GREEN: 등록 지점 확인 (미등록 시 차단)
            String grade = userInfo.getMembershipGrade();
            if (grade.equalsIgnoreCase("GREEN 2") || grade.equalsIgnoreCase("EARLY GREEN")) {
                if (!benefitDao.hasGreenBranchByBranchName(conn, userInfo.getUserId(), branchName)) {
                    System.out.println("[무료주차] GREEN 2 / EARLY GREEN: 해당 지점에 그린 차량이 등록되어 있지 않습니다.");
                    return false;
                }
                // 등록 지점 확인 후, 아래 1일 1회·3시간 제한 로직을 동일하게 적용
            }

            // 오늘 주차 이력 확인 (1일 1회, 3시간 제한)
            ParkingHistoryDto todayHistory = benefitDao.getTodayParkingHistory(conn, userInfo.getVehicleId());
            if (todayHistory != null) {
                if (todayHistory.getExitDate() == null) {
                    long minutes = ChronoUnit.MINUTES.between(todayHistory.getEntryDate(), LocalDateTime.now());
                    if (minutes <= 180) {
                        System.out.println("[무료주차] 현재 주차 중. 입차 후 " + minutes + "분 경과 (3시간 이내 무료)");
                        return true;
                    } else {
                        System.out.println("[무료주차] 3시간 초과. 무료주차 불가.");
                        return false;
                    }
                } else {
                    System.out.println("[무료주차] 오늘 이미 무료주차를 사용했습니다. (1일 1회 제한)");
                    return false;
                }
            }

            System.out.println("무료 주차 가능");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 5. 발레파킹 가능 여부 확인
    // ═══════════════════════════════════════════════════

    /** 발레파킹 판별 (작년 산정적립금 → 정책 확인 → 차량 소유 → 오늘 중복 사용 여부) */
    public boolean checkValetParkingAvailability(String branchName, String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                System.out.println("[발레파킹] 해당 이름의 회원을 찾을 수 없습니다.");
                return false;
            }

            int calculatedAmount = benefitDao.getLastYearCalculatedAmount(conn, userInfo.getUserId());
            if (calculatedAmount == -1) {
                System.out.println("[발레파킹] 작년 산정적립금 이력이 존재하지 않습니다. 발레파킹 조건 미달.");
                return false;
            }

            boolean policyOk = benefitDao.isValetPolicyAvailable(conn, userInfo.getMembershipId(), branchName,
                    calculatedAmount);
            if (!policyOk) {
                System.out.println("[발레파킹] 해당 멤버십/지점/적립금 조건으로 발레파킹이 불가합니다.");
                return false;
            }

            int vehicleId = benefitDao.getVehicleIdByCarNumber(conn, userInfo.getUserId(), carNumber);
            if (vehicleId == -1) {
                System.out.println("[발레파킹] 입력한 차량번호가 해당 회원의 등록차량과 일치하지 않습니다.");
                return false;
            }

            if (benefitDao.hasValetHistoryToday(conn, vehicleId)) {
                System.out.println("[발레파킹] 오늘 이미 발레파킹 서비스를 사용했습니다.");
                return false;
            }

            System.out.println("발레 파킹 가능");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
            int amount = benefitDao.getSpecialDiscountBalance(conn, name);
            if (amount == -1) {
                System.out.println("[특별할인] 해당 이름(" + name + ")의 회원을 찾을 수 없습니다.");
            } else if (amount == 0) {
                System.out.println("[특별할인] " + name + "님의 특별할인 잔액이 없습니다.");
            } else {
                System.out.println("[특별할인] " + name + "님의 현재 특별할인 잔액은 " + amount + "원 입니다.");
            }
            return amount;
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
    public void modifyGreenBranch(String name, String newBranchName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = benefitDao.getUserInfoByName(conn, name);
            if (userInfo == null) {
                System.out.println("[지점 변경 실패] 해당 이름(" + name + ")의 회원을 찾을 수 없습니다.");
                return;
            }
            int userId = userInfo.getUserId();

            int newBranchId = benefitDao.getBranchIdByName(conn, newBranchName);
            if (newBranchId == -1) {
                System.out.println("[지점 변경 실패] 입력하신 지점명(" + newBranchName + ")을 찾을 수 없습니다.");
                return;
            }

            int modifiedCount = benefitDao.getGreenBranchModifiedCount(conn, userId);

            GreenVehicleBranchDto branchDto = new GreenVehicleBranchDto();
            branchDto.setUserId(userId);
            branchDto.setBranchId(newBranchId);

            if (modifiedCount == -1) {
                benefitDao.insertGreenBranch(conn, branchDto);
                conn.commit();
                System.out.println("[지점 등록 성공] " + newBranchName + " 지점으로 최초 등록 완료되었습니다.");
                return;
            }

            if (modifiedCount >= 2) {
                System.out.println("[지점 변경 실패] 지점은 최대 2회까지만 변경 가능합니다. (현재 " + modifiedCount + "회 사용)");
                return;
            }

            benefitDao.updateGreenBranch(conn, branchDto);
            conn.commit();
            System.out.println("[지점 변경 성공] 변경 횟수: " + (modifiedCount + 1) + "/2회 사용");

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            System.out.println("[지점 변경 실패] " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(conn);
        }
    }

    // ═══════════════════════════════════════════════════
    // 8. 리워드 지급 이력 조회
    // ═══════════════════════════════════════════════════

    public void checkRewardHistory(String name) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            List<RewardHistoryDto> list = benefitDao.getRewardHistoryByName(conn, name);
            if (list.isEmpty()) {
                System.out.println("[리워드] " + name + "님의 리워드 지급 이력이 없습니다.");
                return;
            }
            System.out.println("[리워드] " + name + "님의 리워드 지급 이력 (" + list.size() + "건)");
            for (RewardHistoryDto dto : list) {
                System.out.println("  - 지급일: " + dto.getOfferDate() + " | 지급금액: " + dto.getRewardAmount() + "원");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn);
        }
    }
}
