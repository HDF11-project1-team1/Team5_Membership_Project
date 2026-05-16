package master.dao;

import common.jdbc.JdbcTemplate;
import master.dto.PaymentDto;

import java.util.List;

public class PaymentDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int insertPayment(PaymentDto paymentDto) {
        return insertPaymentAndReturnId(paymentDto) > 0 ? 1 : 0;
    }

    public int insertPaymentAndReturnId(PaymentDto paymentDto) {
        Integer paymentId = jdbcTemplate.queryForObject("SELECT seq_payment.NEXTVAL FROM dual", rs -> rs.getInt(1));
        if (paymentId == null) {
            return 0;
        }

        String sql = "INSERT INTO payment (payment_id, payment_type) VALUES (?, ?)";
        int result = jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, paymentId);
            pstmt.setString(2, paymentDto.getPaymentType());
        });

        return result > 0 ? paymentId : 0;
    }

    public List<PaymentDto> selectAllPayments() {
        String sql = "SELECT payment_id, payment_type FROM payment ORDER BY payment_id";
        return jdbcTemplate.query(sql, rs -> new PaymentDto(
                rs.getInt("payment_id"),
                rs.getString("payment_type")
        ));
    }

    public PaymentDto selectPaymentById(int paymentId) {
        String sql = "SELECT payment_id, payment_type FROM payment WHERE payment_id = ?";
        return jdbcTemplate.queryForObject(sql,
                pstmt -> pstmt.setInt(1, paymentId),
                rs -> new PaymentDto(
                        rs.getInt("payment_id"),
                        rs.getString("payment_type")
                ));
    }

    public int updatePayment(PaymentDto paymentDto) {
        String sql = "UPDATE payment SET payment_type = ? WHERE payment_id = ?";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setString(1, paymentDto.getPaymentType());
            pstmt.setInt(2, paymentDto.getPaymentId());
        });
    }

    public int deletePayment(int paymentId) {
        String sql = "DELETE FROM payment WHERE payment_id = ?";
        return jdbcTemplate.update(sql, pstmt -> pstmt.setInt(1, paymentId));
    }

    public boolean existsPaymentId(int paymentId) {
        String sql = "SELECT COUNT(*) FROM payment WHERE payment_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, paymentId));
    }

    public boolean existsPaymentType(String paymentType) {
        String sql = "SELECT COUNT(*) FROM payment WHERE payment_type = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setString(1, paymentType));
    }

    public boolean existsPurchaseByPaymentId(int paymentId) {
        String sql = "SELECT COUNT(*) FROM purchase WHERE payment_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, paymentId));
    }
}

