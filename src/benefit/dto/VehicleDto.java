package benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {

    private int vehicleId;
    private int userId;
    private String carNumber;
    private LocalDateTime registeredDate;
}
