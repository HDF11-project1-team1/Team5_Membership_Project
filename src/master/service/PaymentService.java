package master.service;

import common.exception.DuplicateException;
import common.exception.NotFoundException;
import common.exception.ValidationException;
import master.dao.PaymentDao;
import master.dto.PaymentDto;
import master.dto.request.PaymentRegisterRequestDto;
import policy.service.DefaultPolicyService;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class PaymentService {

    private final PaymentDao paymentDao = new PaymentDao();
    private final DefaultPolicyService defaultPolicyService = new DefaultPolicyService();

    // ===== 결제수단 등록 =====
    public boolean registerPayment(String paymentType) {
        if (!hasText(paymentType)) {
            throw new ValidationException("결제수단명은 필수입니다.");
        }
        if (paymentDao.existsPaymentType(paymentType)) {
            throw new DuplicateException("이미 등록된 결제수단입니다.");
        }

        PaymentDto paymentDto = new PaymentDto(0, paymentType);
        int paymentId = paymentDao.insertPaymentAndReturnId(paymentDto);
        if (paymentId <= 0) {
            return false;
        }

        PaymentRegisterRequestDto requestDto = new PaymentRegisterRequestDto();
        requestDto.setPaymentType(paymentType);

        return defaultPolicyService.createDefaultPoliciesForNewPayment(paymentId, requestDto);
    }

    // ===== 결제수단 목록 조회 =====
    public List<PaymentDto> getPaymentList() {
        return paymentDao.selectAllPayments();
    }

    // ===== 결제수단 상세 조회 =====
    public PaymentDto getPaymentDetail(int paymentId) {
        if (!isValidId(paymentId)) {
            throw new ValidationException("결제수단 ID는 1 이상이어야 합니다.");
        }
        PaymentDto payment = paymentDao.selectPaymentById(paymentId);
        if (payment == null) {
            throw new NotFoundException("결제수단을 찾을 수 없습니다.");
        }
        return payment;
    }

    // ===== 결제수단 수정 =====
    public boolean updatePayment(int paymentId, String paymentType) {
        if (!isValidId(paymentId) || !hasText(paymentType)) {
            throw new ValidationException("결제수단 ID와 결제수단명은 필수입니다.");
        }
        if (!paymentDao.existsPaymentId(paymentId)) {
            throw new NotFoundException("수정할 결제수단을 찾을 수 없습니다.");
        }

        PaymentDto paymentDto = new PaymentDto(paymentId, paymentType);
        return paymentDao.updatePayment(paymentDto) > 0;
    }
}
