package policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoungePolicyDto {

    private int loungePolicyId;
    private int branchId;
    private int loungeId;
    private int membershipId;
    private boolean loungeAvailable;
}

