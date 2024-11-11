package club.cybecraftman.leek.domain.admin;

import club.cybecraftman.leek.repo.admin.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class TokenService {

    @Value("${leek.admin.token.secret: boom-shaka-laka}")
    private String secret;

    /**
     * 过期时间
     */
    @Value("${leek.admin.token.expire-interval: 7200000}")
    private Long expired;

    @Value("${leek.admin.refresh-token.secret: boom-shaka-laka")
    private String refreshSecret;

    /**
     * 续订token的过期时间
     */
    @Value("${leek.admin.refresh-token.expire-interval: 604800000}")
    private Long refreshExpired;

    /**
     * 生成Jwt Token
     * @param user
     * @return
     */
    public String generate(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expired)) // 10小时过期
                .signWith(SignatureAlgorithm.HS256, this.secret)
                .compact();
    }

    /**
     * 生成refresh token
     * @param user
     * @return
     */
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.refreshExpired)) // 10小时过期
                .signWith(SignatureAlgorithm.HS256, this.refreshSecret)
                .compact();
    }

}
