package club.cybecraftman.leek.dto.account;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TokenRenewReqDTO {

    private String refreshToken;

}
