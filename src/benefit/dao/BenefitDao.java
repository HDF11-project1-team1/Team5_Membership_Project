package benefit.dao;

import benefit.dto.GreenVehicleBranchDto;
import benefit.dto.ParkingHistoryDto;
import benefit.dto.RewardHistoryDto;
import benefit.dto.UserInfoDto;
import benefit.dto.VehicleDto;

import common.connection.DBConnection;
import common.connection.DBType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class BenefitDao {

    // 멤버십 등급, 지점명, 라운지명으로 라운지 이용 가능 여부를 조회한다.
    public boolean selectLoungePolicyAvailable(Connection conn, String membershipGrade, String branchName,
            String loungeName) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT lp.lounge_available " +
                    "FROM lounge_policy lp " +
                    "JOIN membership m ON lp.membership_id = m.membership_id " +
                    "JOIN branch b ON lp.branch_id = b.branch_id " +
                    "JOIN lounge l ON lp.lounge_id = l.lounge_id " +
                    "WHERE m.membership_grade = ? " +
                    "  AND b.branch_name = ? " +
                    "  AND l.lounge_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, membershipGrade);
            pstmt.setString(2, branchName);
            pstmt.setString(3, loungeName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("lounge_available") > 0;
            }
            return false;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }


    // 멤버십 등급별 Cafe-H 무료 커피 제공 개수를 조회한다.
    public int selectCafeHPolicyCount(Connection conn, String membershipGrade) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT coffee_count FROM membership WHERE membership_grade = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, membershipGrade);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("coffee_count");
            }
            return 0;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }


    // 회원명으로 회원 ID, 멤버십 ID, 멤버십 등급을 조회한다.
    public UserInfoDto selectUserInfoByName(Connection conn, String name) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT u.user_id, u.membership_id, m.membership_grade " +
                    "FROM users u " +
                    "JOIN membership m ON u.membership_id = m.membership_id " +
                    "WHERE u.name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                UserInfoDto dto = new UserInfoDto();
                dto.setUserId(rs.getInt("user_id"));
                dto.setMembershipId(rs.getInt("membership_id"));
                dto.setMembershipGrade(rs.getString("membership_grade"));
                return dto;
            }
            return null;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 회원이 등록한 차량 수를 조회한다.
    public int selectVehicleCount(Connection conn, int userId) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) AS cnt FROM vehicle WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next())
                return rs.getInt("cnt");
            return 0;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 회원 차량 정보를 등록한다.
    public int insertVehicle(Connection conn, VehicleDto vehicle) throws Exception {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO vehicle (vehicle_id, user_id, car_number, registered_date) " +
                    "VALUES (SEQ_VEHICLE.NEXTVAL, ?, ?, SYSDATE)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, vehicle.getUserId());
            pstmt.setString(2, vehicle.getCarNumber());
            return pstmt.executeUpdate();
        } finally {
            DBConnection.close(pstmt);
        }
    }

    // 차량 등록일을 조회한다.
    public java.sql.Date selectVehicleRegisteredDate(Connection conn, int userId, String carNumber) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT registered_date FROM vehicle WHERE user_id = ? AND car_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, carNumber);
            rs = pstmt.executeQuery();
            if (rs.next())
                return rs.getDate("registered_date");
            return null;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 등록 차량번호를 변경하고 등록일을 갱신한다.
    public int updateVehicle(Connection conn, VehicleDto newVehicle, String oldCarNumber) throws Exception {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE vehicle SET car_number = ?, registered_date = SYSDATE " +
                    "WHERE user_id = ? AND car_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newVehicle.getCarNumber());
            pstmt.setInt(2, newVehicle.getUserId());
            pstmt.setString(3, oldCarNumber);
            return pstmt.executeUpdate();
        } finally {
            DBConnection.close(pstmt);
        }
    }


    // 회원명과 차량번호로 무료주차/발레파킹 판단에 필요한 정보를 조회한다.
    public UserInfoDto selectUserAndVehicleInfo(Connection conn, String name, String carNumber) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT v.vehicle_id, u.user_id, u.membership_id, m.membership_grade " +
                    "FROM vehicle v " +
                    "JOIN users u ON v.user_id = u.user_id " +
                    "JOIN membership m ON u.membership_id = m.membership_id " +
                    "WHERE u.name = ? AND v.car_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, carNumber);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                UserInfoDto dto = new UserInfoDto();
                dto.setVehicleId(rs.getInt("vehicle_id"));
                dto.setUserId(rs.getInt("user_id"));
                dto.setMembershipId(rs.getInt("membership_id"));
                dto.setMembershipGrade(rs.getString("membership_grade"));
                return dto;
            }
            return null;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 멤버십과 지점 기준 무료주차 정책 적용 여부를 조회한다.
    public boolean selectFreeParkingPolicy(Connection conn, int membershipId, String branchName) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT fp.free_parking_available " +
                    "FROM free_parking_policy fp " +
                    "JOIN branch b ON fp.branch_id = b.branch_id " +
                    "WHERE fp.membership_id = ? AND b.branch_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, membershipId);
            pstmt.setString(2, branchName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("free_parking_available") > 0;
            }
            return false;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // GREEN 등급 회원의 등록 지점 여부를 확인한다.
    public boolean existsGreenBranchByBranchName(Connection conn, int userId, String branchName) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT gvb.green_vehicle_branch_id " +
                    "FROM green_vehicle_branch gvb " +
                    "JOIN branch b ON gvb.branch_id = b.branch_id " +
                    "WHERE gvb.user_id = ? AND b.branch_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, branchName);
            rs = pstmt.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 해당 차량의 당일 주차 이력을 조회한다.
    public ParkingHistoryDto selectTodayParkingHistory(Connection conn, int vehicleId) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT ph.entry_date, ph.exit_date " +
                    "FROM parking_history ph " +
                    "WHERE ph.vehicle_id = ? " +
                    "  AND TRUNC(ph.entry_date) = TRUNC(SYSDATE) " +
                    "ORDER BY ph.entry_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, vehicleId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ParkingHistoryDto dto = new ParkingHistoryDto();
                Timestamp entry = rs.getTimestamp("entry_date");
                Timestamp exit = rs.getTimestamp("exit_date");
                if (entry != null)
                    dto.setEntryDate(entry.toLocalDateTime());
                if (exit != null)
                    dto.setExitDate(exit.toLocalDateTime());
                return dto;
            }
            return null;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }


    // 회원의 전년도 VIP 산정금액을 조회한다.
    public int selectLastYearCalculatedAmount(Connection conn, int userId) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT mh.calculated_amount " +
                    "FROM membership_history mh " +
                    "WHERE mh.user_id = ? " +
                    "  AND EXTRACT(YEAR FROM mh.start_date) = EXTRACT(YEAR FROM SYSDATE) - 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int amount = rs.getInt("calculated_amount");
                return rs.wasNull() ? -1 : amount;
            }
            return -1;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 멤버십, 지점, 산정금액 기준 발레파킹 정책 적용 여부를 확인한다.
    public boolean existsValetPolicyAvailable(Connection conn, int membershipId, String branchName, int calculatedAmount)
            throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT vp.valet_available " +
                    "FROM valet_policy vp " +
                    "JOIN branch b ON vp.branch_id = b.branch_id " +
                    "WHERE vp.membership_id = ? " +
                    "  AND b.branch_name = ? " +
                    "  AND ? >= NVL(vp.last_year_vip_min_standard, 0) " +
                    "  AND ? <= NVL(vp.last_year_vip_max_standard, 999999999)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, membershipId);
            pstmt.setString(2, branchName);
            pstmt.setInt(3, calculatedAmount);
            pstmt.setInt(4, calculatedAmount);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("valet_available") > 0;
            }
            return false;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 회원 ID와 차량번호로 차량 ID를 조회한다.
    public int selectVehicleIdByCarNumber(Connection conn, int userId, String carNumber) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT v.vehicle_id FROM vehicle v WHERE v.user_id = ? AND v.car_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, carNumber);
            rs = pstmt.executeQuery();
            if (rs.next())
                return rs.getInt("vehicle_id");
            return -1;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 해당 차량이 오늘 발레파킹을 이미 사용했는지 확인한다.
    public boolean existsValetHistoryToday(Connection conn, int vehicleId) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT ph.valet_use_yn " +
                    "FROM parking_history ph " +
                    "WHERE ph.vehicle_id = ? " +
                    "  AND TRUNC(ph.entry_date) = TRUNC(SYSDATE) " +
                    "  AND ph.valet_use_yn = 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, vehicleId);
            rs = pstmt.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }


    // 회원의 특별할인 잔액을 조회한다.
    public int selectSpecialDiscountBalance(Connection conn, String name) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT ud.remain_special_discount_amount " +
                    "FROM user_detail ud " +
                    "JOIN users u ON ud.user_id = u.user_id " +
                    "WHERE u.name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int amount = rs.getInt("remain_special_discount_amount");
                return rs.wasNull() ? 0 : amount;
            }
            return -1;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }


    // 지점명으로 지점 ID를 조회한다.
    public int selectBranchIdByName(Connection conn, String branchName) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT branch_id FROM branch WHERE branch_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, branchName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("branch_id");
            }
            return -1;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // GREEN 등급 등록 지점 변경 횟수를 조회한다.
    public int selectGreenBranchModifiedCount(Connection conn, int userId) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT modified_count FROM green_vehicle_branch WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("modified_count");
                return rs.wasNull() ? 0 : count;
            }
            return -1;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // GREEN 등급 회원의 이용 지점을 등록한다.
    public int insertGreenBranch(Connection conn, GreenVehicleBranchDto branchDto) throws Exception {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO green_vehicle_branch (green_vehicle_branch_id, user_id, branch_id, modified_count) " +
                    "VALUES (SEQ_GREEN_VEHICLE_BRANCH.NEXTVAL, ?, ?, 0)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, branchDto.getUserId());
            pstmt.setInt(2, branchDto.getBranchId());
            return pstmt.executeUpdate();
        } finally {
            DBConnection.close(pstmt);
        }
    }

    // GREEN 등급 회원의 이용 지점을 변경한다.
    public int updateGreenBranch(Connection conn, GreenVehicleBranchDto branchDto) throws Exception {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE green_vehicle_branch " +
                    "SET branch_id = ?, modified_count = modified_count + 1 " +
                    "WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, branchDto.getBranchId());
            pstmt.setInt(2, branchDto.getUserId());
            return pstmt.executeUpdate();
        } finally {
            DBConnection.close(pstmt);
        }
    }


    // 회원명으로 리워드 제공 이력을 조회한다.
    public java.util.List<RewardHistoryDto> selectRewardHistoryByName(Connection conn, String name) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT rh.reward_history_id, rh.user_id, rh.reward_amount, rh.offer_date " +
                    "FROM reward_history rh " +
                    "JOIN users u ON rh.user_id = u.user_id " +
                    "WHERE u.name = ? " +
                    "ORDER BY rh.offer_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            java.util.List<RewardHistoryDto> list = new java.util.ArrayList<>();
            while (rs.next()) {
                RewardHistoryDto dto = new RewardHistoryDto();
                dto.setRewardHistoryId(rs.getInt("reward_history_id"));
                dto.setUserId(rs.getInt("user_id"));
                dto.setRewardAmount(rs.getInt("reward_amount"));
                java.sql.Date offerDate = rs.getDate("offer_date");
                if (offerDate != null) {
                    dto.setOfferDate(offerDate.toLocalDate());
                }
                list.add(dto);
            }
            return list;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
        }
    }

    // 라운지 이용 가능 여부 조회 기능을 수행한다.
    public boolean selectLoungePolicyAvailability(String membershipGrade, String branchName, String loungeName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return selectLoungePolicyAvailable(conn, membershipGrade, branchName, loungeName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn);
        }
    }

    // Cafe-H 무료 커피 제공 개수 조회 기능을 수행한다.
    public int selectCafeHPolicyCount(String membershipGrade) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return selectCafeHPolicyCount(conn, membershipGrade);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            DBConnection.close(conn);
        }
    }

    // 회원명으로 차량 등록을 처리한다.
    public String insertVehicleByUserName(String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = selectUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[차량 등록 실패] 회원을 찾을 수 없습니다.";
            }

            int userId = userInfo.getUserId();
            String grade = userInfo.getMembershipGrade();
            if (grade.equalsIgnoreCase("BASIC")) {
                return "[차량 등록 실패] BASIC 등급은 차량을 등록할 수 없습니다.";
            }

            int currentCount = selectVehicleCount(conn, userId);
            boolean isJasminTop = grade.equalsIgnoreCase("JASMIN SIGNATURE") || grade.equalsIgnoreCase("JASMIN BLACK");
            int maxLimit = isJasminTop ? 2 : 1;
            if (currentCount >= maxLimit) {
                return "[차량 등록 실패] 차량 등록 가능 대수를 초과했습니다.";
            }

            VehicleDto vehicleDto = new VehicleDto();
            vehicleDto.setUserId(userId);
            vehicleDto.setCarNumber(carNumber);
            insertVehicle(conn, vehicleDto);

            conn.commit();
            return "[차량 등록 성공] 차량번호: " + carNumber;
        } catch (Exception e) {
            rollback(conn);
            return "[차량 등록 실패] " + e.getMessage();
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    // 회원명으로 등록 차량 변경을 처리한다.
    public String updateVehicleByUserName(String name, String oldCarNumber, String newCarNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = selectUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[차량 변경 실패] 회원을 찾을 수 없습니다.";
            }

            int userId = userInfo.getUserId();
            java.sql.Date regDate = selectVehicleRegisteredDate(conn, userId, oldCarNumber);
            if (regDate == null) {
                return "[차량 변경 실패] 등록된 차량을 찾을 수 없습니다.";
            }

            Calendar regCal = Calendar.getInstance();
            regCal.setTime(regDate);
            Calendar nowCal = Calendar.getInstance();
            boolean sameMonth = regCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                    && regCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH);
            if (sameMonth) {
                return "[차량 변경 실패] 이번 달에 이미 변경했습니다.";
            }

            VehicleDto newVehicle = new VehicleDto();
            newVehicle.setUserId(userId);
            newVehicle.setCarNumber(newCarNumber);
            int updated = updateVehicle(conn, newVehicle, oldCarNumber);
            if (updated == 0) {
                return "[차량 변경 실패] 변경된 차량이 없습니다.";
            }

            conn.commit();
            return "[차량 변경 성공] " + oldCarNumber + " -> " + newCarNumber;
        } catch (Exception e) {
            rollback(conn);
            return "[차량 변경 실패] " + e.getMessage();
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    // 무료주차 이용 가능 여부를 조회한다.
    public String selectFreeParkingAvailability(String branchName, String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);

            UserInfoDto userInfo = selectUserAndVehicleInfo(conn, name, carNumber);
            if (userInfo == null) {
                return "[무료주차] 회원 또는 등록 차량을 찾을 수 없습니다.";
            }

            boolean hasPolicy = selectFreeParkingPolicy(conn, userInfo.getMembershipId(), branchName);
            if (!hasPolicy) {
                return "[무료주차] 해당 지점/등급의 무료주차 정책이 없습니다.";
            }

            String grade = userInfo.getMembershipGrade();
            if (grade.equalsIgnoreCase("GREEN 2") || grade.equalsIgnoreCase("EARLY GREEN")) {
                if (!existsGreenBranchByBranchName(conn, userInfo.getUserId(), branchName)) {
                    return "[무료주차] GREEN 등급은 등록 지점에서만 이용 가능합니다.";
                }
            }

            ParkingHistoryDto todayHistory = selectTodayParkingHistory(conn, userInfo.getVehicleId());
            if (todayHistory != null) {
                if (todayHistory.getExitDate() == null) {
                    long minutes = ChronoUnit.MINUTES.between(todayHistory.getEntryDate(), LocalDateTime.now());
                    return minutes <= 180 ? "[무료주차] 현재 무료주차 가능" : "[무료주차] 3시간 초과";
                }
                return "[무료주차] 오늘 이미 무료주차를 사용했습니다.";
            }

            return "[무료주차] 무료주차 가능";
        } catch (Exception e) {
            return "[무료주차] 오류 발생: " + e.getMessage();
        } finally {
            DBConnection.close(conn);
        }
    }

    // 발레파킹 이용 가능 여부를 조회한다.
    public String selectValetParkingAvailability(String branchName, String name, String carNumber) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);

            UserInfoDto userInfo = selectUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[발레파킹] 회원을 찾을 수 없습니다.";
            }

            int calculatedAmount = selectLastYearCalculatedAmount(conn, userInfo.getUserId());
            if (calculatedAmount == -1) {
                return "[발레파킹] 전년도 산정금액 이력이 없습니다.";
            }

            boolean policyOk = existsValetPolicyAvailable(conn, userInfo.getMembershipId(), branchName, calculatedAmount);
            if (!policyOk) {
                return "[발레파킹] 정책 조건에 맞지 않습니다.";
            }

            int vehicleId = selectVehicleIdByCarNumber(conn, userInfo.getUserId(), carNumber);
            if (vehicleId == -1) {
                return "[발레파킹] 등록 차량을 찾을 수 없습니다.";
            }

            if (existsValetHistoryToday(conn, vehicleId)) {
                return "[발레파킹] 오늘 이미 사용했습니다.";
            }

            return "[발레파킹] 발레파킹 가능";
        } catch (Exception e) {
            return "[발레파킹] 오류 발생: " + e.getMessage();
        } finally {
            DBConnection.close(conn);
        }
    }

    // 회원명으로 특별할인 잔액 조회 기능을 수행한다.
    public int selectSpecialDiscountBalance(String name) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return selectSpecialDiscountBalance(conn, name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            DBConnection.close(conn);
        }
    }

    // 회원명으로 GREEN 등급 이용 지점 변경을 처리한다.
    public String updateGreenBranchByUserName(String name, String newBranchName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            UserInfoDto userInfo = selectUserInfoByName(conn, name);
            if (userInfo == null) {
                return "[지점 변경 실패] 회원을 찾을 수 없습니다.";
            }

            int newBranchId = selectBranchIdByName(conn, newBranchName);
            if (newBranchId == -1) {
                return "[지점 변경 실패] 지점을 찾을 수 없습니다.";
            }

            int modifiedCount = selectGreenBranchModifiedCount(conn, userInfo.getUserId());
            GreenVehicleBranchDto branchDto = new GreenVehicleBranchDto();
            branchDto.setUserId(userInfo.getUserId());
            branchDto.setBranchId(newBranchId);

            if (modifiedCount == -1) {
                insertGreenBranch(conn, branchDto);
                conn.commit();
                return "[지점 등록 성공] " + newBranchName;
            }

            if (modifiedCount >= 2) {
                return "[지점 변경 실패] 지점 변경 가능 횟수를 초과했습니다.";
            }

            updateGreenBranch(conn, branchDto);
            conn.commit();
            return "[지점 변경 성공] " + newBranchName;
        } catch (Exception e) {
            rollback(conn);
            return "[지점 변경 실패] " + e.getMessage();
        } finally {
            resetAutoCommit(conn);
            DBConnection.close(conn);
        }
    }

    // 회원명으로 리워드 이력 조회 기능을 수행한다.
    public List<RewardHistoryDto> selectRewardHistory(String name) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            return selectRewardHistoryByName(conn, name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        } finally {
            DBConnection.close(conn);
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void resetAutoCommit(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
