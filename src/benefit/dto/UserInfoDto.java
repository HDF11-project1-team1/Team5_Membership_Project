package benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private int userId;
    private int membershipId;
    private String membershipGrade;
    private int vehicleId; // 무료주차/발레파킹 조회를 위해 추가
}
