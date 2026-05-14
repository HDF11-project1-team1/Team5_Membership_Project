package policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValetPolicyDto {

    private int valetPolicyId;
    private int branchId;
    private int membershipId;
    private int lastYearVipMinStandard;
    private int lastYearVipMaxStandard;
    private boolean valetAvailable;
}
