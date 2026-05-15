package user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTotalInfoDto {

    // 기본정보
    private int userId;
    private int membershipId;
    private String name;
    private String gender;
    private String phoneNumber;
    private LocalDateTime birth;
    private String cardNumber;
    private LocalDateTime cardPeriod;
    private boolean employeeYn;

    // ?곸꽭?뺣낫
    private int vipAmount;
    private int mileageAmount;
    private int totalRewardAmount;
    private int remainSpecialDiscountAmount;
    private int remainCoffee;
    private int visitDateCount;
    private int purchaseDateCount;
}

