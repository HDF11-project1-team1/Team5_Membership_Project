package benefit.dao;

import benefit.dto.CoffeeHistoryDto;
import benefit.dto.LoungeHistoryDto;
import benefit.dto.ParkingHistoryDto;

import common.connection.DBConnection;
import common.connection.DBType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BenefitDao {

    // 1. (멤버십명, 지점명, 라운지명)으로 라운지 정책 이용가능 여부 조회
    public boolean findLoungePolicyAvailabilityByName(String membershipGrade, String branchName, String loungeName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean available = false;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
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
                int isAvailable = rs.getInt("lounge_available");
                available = (isAvailable > 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return available;
    }

    // 2. (멤버십명)으로 Cafe-H 제공 횟수 정책 조회
    public int findCafeHPolicyCountByName(String membershipGrade) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);
            String sql = "SELECT coffee_count FROM membership WHERE membership_grade = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, membershipGrade);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("coffee_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
        return count;
    }

    /**
     * 3. 무료주차 가능 여부 확인
     * - 조건1: vehicle 테이블에서 회원의 차량번호 일치 여부 확인 및 회원 멤버십 정보 조회
     * - 조건2: free_parking_policy에서 지점+회원 멤버십으로 무료주차 가능 여부 조회
     * - GREEN 2 / EARLY GREEN: green_vehicle_branch에서 회원+지점 등록 여부 추가 확인
     * - 그 외 등급: parking_history에서 오늘 1일 1회 & 3시간 이내 확인
     *
     * @param branchName      지점 이름
     * @param name            회원 이름
     * @param carNumber       차량 번호
     * @return 무료주차 가능 여부
     */
    public boolean findFreeParkingAvailability(String branchName, String name, String carNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(DBType.ORACLE);

            // ── STEP 1: 회원의 차량번호 확인 및 멤버십 정보 가져오기 ──
            String vehicleSQL = "SELECT v.vehicle_id, u.user_id, u.membership_id, m.membership_grade " +
                    "FROM vehicle v " +
                    "JOIN users u ON v.user_id = u.user_id " +
                    "JOIN membership m ON u.membership_id = m.membership_id " +
                    "WHERE u.name = ? AND v.car_number = ?";
            pstmt = conn.prepareStatement(vehicleSQL);
            pstmt.setString(1, name);
            pstmt.setString(2, carNumber);
            rs = pstmt.executeQuery();

            int vehicleId = -1;
            int userId = -1;
            int membershipId = -1;
            String membershipGrade = "";

            if (rs.next()) {
                vehicleId = rs.getInt("vehicle_id");
                userId = rs.getInt("user_id");
                membershipId = rs.getInt("membership_id");
                membershipGrade = rs.getString("membership_grade");
            }
            DBConnection.close(rs);
            DBConnection.close(pstmt);

            if (vehicleId == -1) {
                System.out.println("[무료주차] 해당 회원의 등록 차량과 입력 차량번호가 일치하지 않거나 회원을 찾을 수 없습니다.");
                return false;
            }

            // ── STEP 2: free_parking_policy에서 지점+멤버십 기반 무료주차 정책 조회 ──
            String policySQL = "SELECT fp.free_parking_available " +
                    "FROM free_parking_policy fp " +
                    "JOIN branch b ON fp.branch_id = b.branch_id " +
                    "WHERE fp.membership_id = ? AND b.branch_name = ?";
            pstmt = conn.prepareStatement(policySQL);
            pstmt.setInt(1, membershipId);
            pstmt.setString(2, branchName);
            rs = pstmt.executeQuery();

            boolean policyAvailable = false;
            if (rs.next()) {
                policyAvailable = rs.getInt("free_parking_available") > 0;
            }
            DBConnection.close(rs);
            DBConnection.close(pstmt);

            if (!policyAvailable) {
                System.out.println("[무료주차] 해당 지점/멤버십 등급은 무료주차 정책이 없습니다.");
                return false;
            }

            // ── STEP 3: GREEN 2 / EARLY GREEN은 그린차량 등록 지점 확인 ──
            if (membershipGrade.equalsIgnoreCase("GREEN 2") || membershipGrade.equalsIgnoreCase("EARLY GREEN")) {
                String greenSQL = "SELECT gvb.green_vehicle_branch_id " +
                        "FROM green_vehicle_branch gvb " +
                        "JOIN branch b ON gvb.branch_id = b.branch_id " +
                        "WHERE gvb.user_id = ? AND b.branch_name = ?";
                pstmt = conn.prepareStatement(greenSQL);
                pstmt.setInt(1, userId);
                pstmt.setString(2, branchName);
                rs = pstmt.executeQuery();

                boolean greenRegistered = rs.next();
                DBConnection.close(rs);
                DBConnection.close(pstmt);

                if (!greenRegistered) {
                    System.out.println("[무료주차] GREEN 2 / EARLY GREEN: 해당 지점에 그린 차량이 등록되어 있지 않습니다.");
                    return false;
                }
                // 그린 등록 지점 확인 완료 → 무료주차 가능
                return true;
            }

            // ── STEP 4: 그 외 등급 - parking_history에서 1일 1회 & 3시간 이내 확인 ──
            // 오늘 날짜 기준으로 입차 기록이 있는지 확인
            String historySQL = "SELECT ph.entry_date, ph.exit_date " +
                    "FROM parking_history ph " +
                    "WHERE ph.vehicle_id = ? " +
                    "  AND TRUNC(ph.entry_date) = TRUNC(SYSDATE)";
            pstmt = conn.prepareStatement(historySQL);
            pstmt.setInt(1, vehicleId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 오늘 이미 입차 이력이 있으면 1일 1회 초과
                Timestamp entryTimestamp = rs.getTimestamp("entry_date");
                Timestamp exitTimestamp = rs.getTimestamp("exit_date");
                DBConnection.close(rs);
                DBConnection.close(pstmt);

                if (exitTimestamp == null) {
                    // 아직 출차 안 된 상태 → 현재 주차 중
                    LocalDateTime entry = entryTimestamp.toLocalDateTime();
                    long minutes = ChronoUnit.MINUTES.between(entry, LocalDateTime.now());
                    if (minutes <= 180) {
                        System.out.println("[무료주차] 현재 주차 중. 입차 후 " + minutes + "분 경과 (3시간 이내 무료)");
                        return true;
                    } else {
                        System.out.println("[무료주차] 3시간 초과. 무료주차 불가.");
                        return false;
                    }
                } else {
                    // 이미 출차 완료된 이력 존재 → 오늘 1회 사용
                    System.out.println("[무료주차] 오늘 이미 무료주차를 사용했습니다. (1일 1회 제한)");
                    return false;
                }
            }

            DBConnection.close(rs);
            DBConnection.close(pstmt);

            // 오늘 입차 이력 없음 → 무료주차 가능
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
    }

}
