package club.cybercraftman.leek.dto.user;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordReqDTO {

    private String oldPassword;

    private String newPassword;

}
