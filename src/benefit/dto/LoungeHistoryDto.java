package benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoungeHistoryDto {

    private int loungeHistoryId;
    private int userId;
    private int loungeId;
    private LocalDateTime entryDate;
}

