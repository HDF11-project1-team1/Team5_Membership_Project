package benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeHistoryDto {

    private int coffeeHistoryId;
    private int userId;
    private LocalDateTime usedDate;
}
