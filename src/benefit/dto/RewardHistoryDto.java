package benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardHistoryDto {
    private int rewardHistoryId;
    private int userId;
    private int rewardAmount;
    private LocalDate offerDate;
}
