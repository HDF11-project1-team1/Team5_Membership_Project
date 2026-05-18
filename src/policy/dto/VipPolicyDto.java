package policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VipPolicyDto {

    private int vipPolicyId;
    private int branchId;
    private int paymentId;
    private int vipRate;
}

