package com.tpe.cinetime.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthTokenFilter authTokenFilter;
    private final JwtUtils jwtUtils;


    //Password encoder -> BCrypt kullaniyorum
    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

    //DB'den kullaniciyi cekip password dogrulayan provider
    /*
      Login sirasinda AuthenticationManager,
      kullaniciyi UserDetailsServiceImpl üzerinden bulur
      ve passwordEncoder ile sifre kontrolü yapar.
    */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    //Login endpoint'de kullanilacak
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }

    //CORS ayarlari -> Frontend'den gelen isteklere izin verme
    @Bean
    public WebMvcConfigurer corsConfigurer(){

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry corsRegistry){
                corsRegistry.addMapping("/**")
                        .allowedOrigins("*") //production'da frontend URL yazilmasi gerek
                        .allowedHeaders("*")
                        .allowedMethods("*");
            }
        };
    }

    //Security kurallarini tanimlama
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity
                .cors()
                .and()
                .csrf().disable()
                //Yetkisiz erisimde AuthenticationEntryPointJwt devreye girecek
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                //Jwt kullandigimiz icin session tutmuyoruz
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated();

        //H2 consel icin (gelistirme ortami)
        httpSecurity.headers().frameOptions().sameOrigin();
        httpSecurity.authenticationProvider(daoAuthenticationProvider());

        //Jwt filter'i, Spring'in kendi login filter'inden önce ekleme
        httpSecurity.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    private static final String[] AUTH_WHITELIST = {
            // Auth
            "/auth/login",
            "/auth/register",

            // Springdoc / Swagger UI
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/webjars/**",

            // Static
            "/",
            "/index.html",
            "/css/**",
            "/js/**",
            "/images/**"
    };


}
