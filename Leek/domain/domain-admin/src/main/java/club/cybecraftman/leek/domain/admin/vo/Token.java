package club.cybecraftman.leek.domain.admin.vo;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class Token {

    private String token;

    private String refreshToken;

}
