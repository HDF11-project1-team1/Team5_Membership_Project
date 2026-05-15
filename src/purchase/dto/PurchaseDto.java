package purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDto {

    private int purchaseId;
    private int userId;
    private int branchId;
    private int brandId;
    private int membershipId;
    private int paymentId;
    private int price;
    private String purchaseStatus;
    private LocalDateTime generatedDate;
}

