package policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardPolicyDto {

    private int rewardPolicyId;
    private int membershipId;
    private int offerStandard;
    private int rewardAmount;
}

