package user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {

    private int userId;
    private int vipAmount;
    private int mileageAmount;
    private int totalRewardAmount;
    private int remainSpecialDiscountAmount;
    private int remainCoffee;
    private int visitDateCount;
    private int purchaseDateCount;
}
