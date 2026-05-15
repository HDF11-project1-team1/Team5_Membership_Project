package benefit.dao;

import benefit.dto.GreenVehicleBranchDto;
import benefit.dto.ParkingHistoryDto;
import benefit.dto.RewardHistoryDto;
import benefit.dto.UserInfoDto;
import benefit.dto.VehicleDto;

import common.connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class BenefitDao {

    // ═══════════════════════════════════════════════════
    // 1. 라운지 이용가능 여부 확인
    // ═══════════════════════════════════════════════════

    /** 멤버십 등급, 지점명, 라운지명으로 라운지 이용가능 여부 조회 */
    public boolean getLoungePolicyAvailable(Connection conn, String membershipGrade, String branchName,
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

    // ═══════════════════════════════════════════════════
    // 2. Cafe-H 커피 지급수 조회
    // ═══════════════════════════════════════════════════

    /** 멤버십 등급별 Cafe-H 무료 커피 제공 횟수 조회 */
    public int getCafeHPolicyCount(Connection conn, String membershipGrade) throws Exception {
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

    // ═══════════════════════════════════════════════════
    // 3. 차량 등록 / 변경
    // ═══════════════════════════════════════════════════

    /** 회원 이름으로 유저 기본 정보(userId, membershipId, grade) 조회 */
    public UserInfoDto getUserInfoByName(Connection conn, String name) throws Exception {
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

    /** 회원의 현재 등록 차량 대수 조회 */
    public int getVehicleCount(Connection conn, int userId) throws Exception {
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

    /** 차량 등록 (INSERT) */
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

    /** 차량의 마지막 등록(변경)일 조회 — 월 1회 변경 제한 검증용 */
    public java.sql.Date getVehicleRegisteredDate(Connection conn, int userId, String carNumber) throws Exception {
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

    /** 차량 번호 변경 (registered_date도 SYSDATE로 갱신) */
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

    // ═══════════════════════════════════════════════════
    // 4. 무료주차 가능 여부 확인
    // ═══════════════════════════════════════════════════

    /** 회원 이름 + 차량번호로 회원·차량 통합 정보 조회 */
    public UserInfoDto getUserAndVehicleInfo(Connection conn, String name, String carNumber) throws Exception {
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

    /** 멤버십 ID + 지점명으로 무료주차 정책 존재 여부 조회 */
    public boolean getFreeParkingPolicy(Connection conn, int membershipId, String branchName) throws Exception {
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

    /** GREEN 등급 회원의 특정 지점 차량 등록 여부 확인 */
    public boolean hasGreenBranchByBranchName(Connection conn, int userId, String branchName) throws Exception {
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

    /** 오늘 입차된 최근 주차 기록 1건 조회 */
    public ParkingHistoryDto getTodayParkingHistory(Connection conn, int vehicleId) throws Exception {
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

    // ═══════════════════════════════════════════════════
    // 5. 발레파킹 가능 여부 확인
    // ═══════════════════════════════════════════════════

    /** 회원의 작년 산정적립금 조회 (기록 없으면 -1) */
    public int getLastYearCalculatedAmount(Connection conn, int userId) throws Exception {
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

    /** 발레파킹 정책(적립금 허용 구간) 존재 여부 조회 */
    public boolean isValetPolicyAvailable(Connection conn, int membershipId, String branchName, int calculatedAmount)
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

    /** 회원 ID + 차량번호로 본인 소유 차량 ID 조회 (없으면 -1) */
    public int getVehicleIdByCarNumber(Connection conn, int userId, String carNumber) throws Exception {
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

    /** 특정 차량의 오늘 발레파킹 사용 이력 존재 여부 확인 */
    public boolean hasValetHistoryToday(Connection conn, int vehicleId) throws Exception {
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

    // ═══════════════════════════════════════════════════
    // 6. 특별할인 잔액 조회
    // ═══════════════════════════════════════════════════

    /** 회원 이름으로 특별할인 잔액 조회 (회원 없으면 -1) */
    public int getSpecialDiscountBalance(Connection conn, String name) throws Exception {
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

    // ═══════════════════════════════════════════════════
    // 7. 그린 차량 지점 등록
    // ═══════════════════════════════════════════════════

    /** 지점명으로 지점 ID 조회 (없으면 -1) */
    public int getBranchIdByName(Connection conn, String branchName) throws Exception {
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

    /** GREEN 등급 회원의 지점 변경 횟수 조회 (미등록 시 -1) */
    public int getGreenBranchModifiedCount(Connection conn, int userId) throws Exception {
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

    /** GREEN 등급 회원의 무료주차 지점 최초 등록 */
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

    /** GREEN 등급 회원의 무료주차 지점 변경 + 변경 횟수 증가 */
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

    // ═══════════════════════════════════════════════════
    // 8. 리워드 지급 이력 조회
    // ═══════════════════════════════════════════════════

    /** 회원 이름으로 리워드 지급 이력 전체 조회 */
    public java.util.List<RewardHistoryDto> getRewardHistoryByName(Connection conn, String name) throws Exception {
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

}
