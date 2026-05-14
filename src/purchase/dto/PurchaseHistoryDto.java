package purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseHistoryDto {

    private int purchaseHistoryId;
    private int userId;
    private int branchId;
    private int categoryId;
    private int brandId;
    private int membershipId;
    private int paymentId;
    private int price;
    private String purchaseStatus;
    private LocalDateTime generatedDate;
    private int discountPrice;
    private int discountRate;
    private int vipAmount;
    private int mileageAmount;
}
