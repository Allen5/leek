package club.cybecraftman.leek.dto.user;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class UserInfoRespDTO {

    private String username;

    private String nickname;

    private String avatar;

    /**
     * 创建时间
     */
    private Date createdAt;

}
