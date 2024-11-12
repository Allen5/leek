package club.cybecraftman.leek.domain.admin;

import club.cybecraftman.leek.repo.admin.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class TokenService {

    @Value("${leek.admin.token.secret: SecretKey012345678901234567890123456789012345678901234567890123456789}")
    private String secret;

    /**
     * 过期时间
     */
    @Value("${leek.admin.token.expire-interval: 7200000}")
    private Long expired;

    @Value("${leek.admin.refresh-token.secret: SecretKey012345678901234567890123456789012345678901234567890123456789")
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

        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expired)) // 10小时过期
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成refresh token
     * @param user
     * @return
     */
    public String generateRefreshToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(this.refreshSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.refreshExpired)) // 10小时过期
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
