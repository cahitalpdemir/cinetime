package com.tpe.cinetime.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            //1. Header'dan token'i cikarma
            String jwt = extractJwtFromRequest(request);

            //2. Token var mi ve gecerli mi?
            if (jwt != null && jwtUtils.validateJwt(jwt)){

                //3. Tokendan emaili al
                String email = jwtUtils.getEmailFromJwt(jwt);

                //4. DB'den kullaniciyi yükleme
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                //5. Authentication nesnesini olustur
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // principal - kim bu kullanici
                        null, // credentials -token varsa sifre gerekmez
                        userDetails.getAuthorities() // roller
                );

                //6. Request detaylarini ekleme(IP, session vs.)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //7.SecurityContext'e set etme -> artik kullanici giris yapmis sayilir
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch (Exception e){
            log.error("Cannot authenticate user: {} ", e.getMessage());
        }

        //8. Bir sonraki filter'a devam et.  !!Bu satir her zaman calismali
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request){

        String header = request.getHeader("Authorization");

        //Header: "Bearer eyJhbGci..." → "eyJhbGci..." döner
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")){
            return header.substring(7);
        }

        return null;
    }
}
