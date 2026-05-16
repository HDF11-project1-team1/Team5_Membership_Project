package master.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoungeRegisterRequestDto {

    private String loungeName;
    private boolean loungeAvailable;
}
