package hello.world.myjwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Component
public class JwtTokenProvider {

    // https://velog.io/@suhyun_zip/JWT-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
    // https://gong-check.github.io/dev-blog/BE/%EC%96%B4%EC%8D%B8%EC%98%A4/jwt_impl/jwt_impl/
    private final SecretKey key;

    private final long expireTime;

    public JwtTokenProvider(@Value("${jwt.token.secret-key}") final String secretKey,
                            @Value("${jwt.token.expire-time}") final long expireTime ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
//        this.key = Jwts.SIG.HS256.key().build();

        this.expireTime = expireTime;
    }

    public String createToken(final String subject) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expireTime);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(nowDate)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }


    public boolean isValidToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e ) {
            // 만료된 토큰
            return false;
        } catch (JwtException e) {
            throw new IllegalStateException("올바른 토큰이 아닙니다");
        }
    }

    public Claims extractPayload(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return this.extractPayload(token).getSubject();
    }

    public String extractIssuer(String token) {
        return this.extractPayload(token).getIssuer();
    }

    public Object extractCustom(String token, String key) {
        return this.extractPayload(token).get(key);
    }

}
