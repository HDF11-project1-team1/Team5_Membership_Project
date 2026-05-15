package membership.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipHistoryDto {
    private int membershipHistoryId;
    private int userId;
    private int membershipId;
    private String membershipGrade;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long calculateAmount;
}
