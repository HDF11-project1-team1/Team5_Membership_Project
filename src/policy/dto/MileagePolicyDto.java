package policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MileagePolicyDto {

    private int mileagePolicyId;
    private int branchId;
    private int brandId;
    private int paymentId;
    private int mileageRate;
}
