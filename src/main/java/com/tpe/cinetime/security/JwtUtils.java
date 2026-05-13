package com.tpe.cinetime.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    //String jwtSecret'i Key nesnesine çeviriyoruz (0.11+ zorunluluğu)
    private Key getSigningKey(){

        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        //getBytes() ile bu String'i byte dizisine çevirioyruz
        //Keys.hmacShaKeyFor() ile byte dizisin alip
        //HMAC-SHA algoritması için uygun bir Key nesnesi üretiyoruz
    }

    //JWT generate
    public String generateJwt(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return generateJwtWithEmail(userDetails.getUsername());
        //getUsername() aslinda email döndürüyor — UserDetailsImpl'da böyle tanımladim
    }

    private String generateJwtWithEmail(String email){

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    //JWT validate
    public boolean validateJwt(String jwt){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt);

            return true;
        }catch (ExpiredJwtException e){
            log.error("JWT is expired: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("JWT is unsupported: {}", e.getMessage());
        }catch (MalformedJwtException e){
            log.error("JWT is malformed: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            log.error("Illegal Argument: {}", e.getMessage());
        }catch (SignatureException e){
            log.error("JWT signature is invalid: {}", e.getMessage());
        }

        return false;
    }

    //Get email from Jwt
    public String getEmailFromJwt(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject(); //email döner
    }
}
