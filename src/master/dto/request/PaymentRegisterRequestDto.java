package master.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRegisterRequestDto {

    private String paymentType;
    private boolean vipAvailable;
    private double defaultVipRate;
    private boolean mileageAvailable;
    private double defaultMileageRate;
}
