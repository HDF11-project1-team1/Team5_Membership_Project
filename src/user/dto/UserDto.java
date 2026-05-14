package user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int userId;
    private int membershipId;
    private String name;
    private String gender;
    private String phoneNumber;
    private LocalDateTime birth;
    private String cardNumber;
    private LocalDateTime cardPeriod;
    private boolean employeeYn;
}
