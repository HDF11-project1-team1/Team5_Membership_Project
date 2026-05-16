package master.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchRegisterRequestDto {

    private String branchName;
    private String branchAddress;
    private String branchType;
    private boolean vipAvailable;
    private double defaultVipRate;
    private boolean mileageAvailable;
    private double defaultMileageRate;
    private boolean valetAvailable;
    private int lastYearVipMinStandard;
    private int lastYearVipMaxStandard;
    private boolean freeParkingAvailable;
    private boolean loungeAvailable;
}
