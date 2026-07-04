package com.tpe.cinetime.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${cinetime.app.accessTokenExpirationMs}")
    private long accessTokenExpirationMs;

    @Value("${cinetime.app.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;

    private final RedisTemplate<String, String> redisTemplate;

    //String jwtSecret'i Key nesnesine çeviriyoruz (0.11+ zorunluluğu)
    private Key getSigningKey(){

        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        //getBytes() ile bu String'i byte dizisine çevirioyruz
        //Keys.hmacShaKeyFor() ile byte dizisin alip
        //HMAC-SHA algoritması için uygun bir Key nesnesi üretiyoruz
    }

    //Access Token generate
    public String generateAccessToken(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return generateAccessTokenWithEmail(userDetails.getUsername());
        //getUsername() aslinda email döndürüyor — UserDetailsImpl'da böyle tanımladim
    }

    private String generateAccessTokenWithEmail(String email){

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    //Access Token validate
    public boolean validateAccessToken(String accessToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(accessToken);

            return true;
        }catch (ExpiredJwtException e){
            log.error("Access token is expired: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("Access token is unsupported: {}", e.getMessage());
        }catch (MalformedJwtException e){
            log.error("Access token is malformed: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            log.error("Illegal Argument: {}", e.getMessage());
        }catch (SignatureException e){
            log.error("Access token signature is invalid: {}", e.getMessage());
        }

        return false;
    }

    //Get email from Access Token
    public String getEmailFromAccessToken(String accessToken){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject(); //email döner
    }

    // YENI!!!! REFRESH TOKEN

    //Refresh token üretme ve redis'ye kaydetme
    public String generateRefreshToken(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshTokenExpirationMs);

        String refreshToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        //Redis'e kaydet
        saveRefreshTokenInRedis(userDetails.getId(), refreshToken);

        return refreshToken;
    }

    //Redis'e kaydet -> key: "refreshToken:{userId}", TTL: 7 gün
    public void saveRefreshTokenInRedis(Long userId, String refreshToken){
        String redisKey = "refreshToken:user:" + userId;
        redisTemplate.opsForValue().set(redisKey,refreshToken, refreshTokenExpirationMs, TimeUnit.MILLISECONDS);
        log.info("Refresh token generated and saved in Redis. Key: {}", redisKey);
    }

    //Refresh token validate
    public boolean validateRefreshToken(String refreshToken){

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(refreshToken);

            return true;
        } catch (ExpiredJwtException e) {
            log.error("Refresh token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Refresh token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Refresh token is malformed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Illegal Argument: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Refresh token signature is invalid: {}", e.getMessage());
        }

        return false;
    }

    //Redis'teki token ile gelen token'ın eşit olup olmadığını kontrol etme
    public boolean isRefreshTokenValid(Long userId, String refreshToken){

        String redisKey = "refreshToken:user:" + userId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if(storedToken == null){
            log.warn("No refresh token found in Redis for user ID: {}", userId);
            return false;
        }

        return storedToken.equals(refreshToken) && validateRefreshToken(refreshToken);
    }

    //Get email from refresh token
    public String getEmailFromRefreshToken(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .getSubject();
    }

    //Logout: Redis'ten refresh token sil
    public void deleteRefreshToken(Long userId){
        String redisKey = "refreshToken:user:" + userId;
        redisTemplate.delete(redisKey);
        log.info("Refresh token removed from Redis for user ID: {}", userId);
    }
}
