package club.cybercraftman.leek.dto.account;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class LoginRespDTO {

    private String token;

    private String refreshToken;

}
