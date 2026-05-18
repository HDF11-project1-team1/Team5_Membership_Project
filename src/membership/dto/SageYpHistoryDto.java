package membership.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SageYpHistoryDto {

    private int sageYpHistoryId;
    private int userId;
    private int sageYpStatusId;
}

