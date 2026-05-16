package master.service;

import master.dao.PaymentDao;
import master.dto.PaymentDto;
import master.dto.request.PaymentRegisterRequestDto;
import policy.service.DefaultPolicyService;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;
import static common.validation.InputValidator.isValidMileageRate;
import static common.validation.InputValidator.isValidRate;

public class PaymentService {

    private final PaymentDao paymentDao = new PaymentDao();
    private final DefaultPolicyService defaultPolicyService = new DefaultPolicyService();

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

    public boolean registerPayment(PaymentRegisterRequestDto requestDto) {
        if (requestDto == null) {
            return false;
        }
        if (!hasText(requestDto.getPaymentType())) {
            return false;
        }
        if (!isValidRate(requestDto.getDefaultVipRate()) || !isValidMileageRate(requestDto.getDefaultMileageRate())) {
            return false;
        }
        if (paymentDao.existsPaymentType(requestDto.getPaymentType())) {
            return false;
        }

        PaymentDto paymentDto = new PaymentDto(0, requestDto.getPaymentType());
        int paymentId = paymentDao.insertPaymentAndReturnId(paymentDto);
        if (!isValidId(paymentId)) {
            return false;
        }

        return defaultPolicyService.createDefaultPoliciesForNewPayment(paymentId, requestDto);
    }

    public List<PaymentDto> findPaymentList() {
        return paymentDao.selectAllPayments();
    }

    public PaymentDto findPaymentDetail(int paymentId) {
        if (!isValidId(paymentId)) {
            return null;
        }
        return paymentDao.selectPaymentById(paymentId);
    }

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
