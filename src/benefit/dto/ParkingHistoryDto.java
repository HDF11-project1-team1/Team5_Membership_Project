package benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingHistoryDto {

    private String parkingHistoryId;
    private int vehicleId;
    private LocalDateTime entryDate;
    private LocalDateTime exitDate;
    private int paymentAmount;
    private boolean valetUseYn;
}

