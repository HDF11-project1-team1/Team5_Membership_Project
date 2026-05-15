package membership.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipDto {

    private int membershipId;
    private String membershipGrade;
    private int minAmount;
    private int maxDiscount;
    private int basicDiscount;
    private int specialDiscountAmount;
    private int coffeeCount;
}

