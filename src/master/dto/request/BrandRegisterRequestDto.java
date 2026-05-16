package master.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandRegisterRequestDto {

    private String brandName;
    private int categoryId;
    private boolean mileageAvailable;
    private double defaultMileageRate;
}
