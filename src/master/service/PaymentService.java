package master.service;

import master.dao.PaymentDao;
import master.dto.PaymentDto;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class PaymentService {

    private final PaymentDao paymentDao = new PaymentDao();

    // ===== 결제수단 등록 =====
    public boolean registerPayment(String paymentType) {
        if (!hasText(paymentType)) {
            return false;
        }
        if (paymentDao.existsPaymentType(paymentType)) {
            return false;
        }

        PaymentDto paymentDto = new PaymentDto(0, paymentType);
        return paymentDao.insertPayment(paymentDto) > 0;
    }

    // ===== 결제수단 목록 조회 =====
    public List<PaymentDto> getPaymentList() {
        return paymentDao.selectAllPayments();
    }

    // ===== 결제수단 상세 조회 =====
    public PaymentDto getPaymentDetail(int paymentId) {
        if (!isValidId(paymentId)) {
            return null;
        }
        return paymentDao.selectPaymentById(paymentId);
    }

    // ===== 결제수단 수정 =====
    public boolean updatePayment(int paymentId, String paymentType) {
        if (!isValidId(paymentId) || !hasText(paymentType)) {
            return false;
        }
        if (!paymentDao.existsPaymentId(paymentId)) {
            return false;
        }

        PaymentDto paymentDto = new PaymentDto(paymentId, paymentType);
        return paymentDao.updatePayment(paymentDto) > 0;
    }
}

