package membership.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MembershipHistoryDto {
    private int membershipHistoryId;
    private int userId;
    private int membershipId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long calculateAmount;

}
