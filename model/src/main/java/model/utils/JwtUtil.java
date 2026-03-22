package model.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    // 注意：密钥长度必须≥32字符（256位），否则会抛出异常
    private static final String SECRET_KEY = "mySecretKey1234567890mySecretKey1234567890";
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000; // 12小时

    /**
     * 生成JWT令牌（兼容写法，避免IDE解析错误）
     */
    public static String generateJwt(Map<String, Object> claims) {
        // 生成HS256密钥
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        // 分步构建，避免链式调用导致的IDE解析问题
        JwtBuilder jwtBuilder = Jwts.builder();
        // 等价于旧版 addClaims()，兼容写法
        jwtBuilder.setClaims(claims);
        // 设置过期时间
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME));
        // 设置签名
        jwtBuilder.signWith(secretKey);

        return jwtBuilder.compact();
    }

    /**
     * 解析JWT令牌（兼容写法）
     */
    public static Map<String, Object> parseJwt(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        // 分步构建解析器，避免链式调用解析错误
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey) // 用旧版兼容的 setSigningKey 替代 verifyWith
                .build();

        // 解析令牌并获取载荷
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        return claims;
    }
}
