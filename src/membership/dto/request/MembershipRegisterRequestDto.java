package membership.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipRegisterRequestDto {

    private String membershipGrade;
    private int minAmount;
    private int maxDiscount;
    private int basicDiscount;
    private int specialDiscountAmount;
    private int coffeeCount;
    private boolean valetAvailable;
    private int lastYearVipMinStandard;
    private int lastYearVipMaxStandard;
    private boolean freeParkingAvailable;
    private boolean loungeAvailable;
    private int rewardOfferStandard;
    private int rewardAmount;
}
