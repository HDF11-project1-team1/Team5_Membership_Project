package policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeParkingPolicyDto {

    private int freeParkingPolicyId;
    private int branchId;
    private int membershipId;
    private boolean freeParkingAvailable;
}
