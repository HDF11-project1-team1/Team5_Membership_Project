package membership.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipCurrentGradeDto {

    private int userId;
    private String name;
    private String phoneNumber;
    private int membershipId;
    private String membershipGrade;
}

