package club.cybercraftman.leek.dto.account;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginReqDTO {

    private String username;

    private String password;

}
