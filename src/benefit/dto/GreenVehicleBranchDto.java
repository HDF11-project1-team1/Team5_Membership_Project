package benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GreenVehicleBranchDto {
    private int greenVehicleBranchId;
    private int userId;
    private int branchId;
    private int modifiedCount;
}
