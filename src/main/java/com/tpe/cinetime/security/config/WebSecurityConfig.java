package com.tpe.cinetime.security.config;

import com.tpe.cinetime.security.AuthTokenFilter;
import com.tpe.cinetime.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;


    //Password encoder -> BCrypt kullaniyorum
    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

    //DB'den kullaniciyi cekip password dogrulayan provider
    /*
      Login sirasinda AuthenticationManager,
      kullaniciyi UserDetailsServiceImpl Ã¼zerinden bulur
      ve passwordEncoder ile sifre kontrolÃ¼ yapar.
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

    // Keep browser access explicit and environment-driven instead of allowing every origin.
    @Bean
    public WebMvcConfigurer corsConfigurer(){

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry corsRegistry){
                corsRegistry.addMapping("/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedHeaders("Authorization", "Content-Type", "Accept")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .maxAge(3600);
            }
        };
    }
    //------------------------------------------

    //app.security.enabled=false ise bu bean aktif olur (her şeye izin verir)
    @Bean
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
    public SecurityFilterChain disabledSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
    //------------------------------------------


    //Security kurallarini tanimlama
    //app.security.enabled=true ise bu bean aktif olur
    @Bean
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
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
                // Public movie read endpoints
                .antMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                // Movie write endpoints require admin or manager role
                .antMatchers(HttpMethod.POST, "/api/movies/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.PUT, "/api/movies/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.DELETE, "/api/movies/**").hasAnyRole("ADMIN", "MANAGER")
                //Role bazli endpoint kurallari
                .antMatchers("/admin/users/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers("/admin/user/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/customer/**").hasRole("CUSTOMER")
                .antMatchers(HttpMethod.DELETE, "/user/me").hasRole("CUSTOMER")
                //YukarÄ±daki kurallar dÄ±ÅŸÄ±nda herhangi bir istek varsa login olmasi gerek
                .anyRequest().authenticated();

        //H2 console icin (gelistirme ortami)
        httpSecurity.headers().frameOptions().sameOrigin();
        httpSecurity.authenticationProvider(daoAuthenticationProvider());

        //Jwt filter'i, Spring'in kendi login filter'inden Ã¶nce ekleme
        //BÃ¶ylece her request'de Ã¶nce Authorization header kontrolÃ¼ yapilir
        httpSecurity.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    private static final String[] AUTH_WHITELIST = {
            //Auth
            "/auth/login",
            "/auth/register",
            "/auth/refresh-token",
            "/auth/forgot-password",
            "/auth/reset-password",
            //Public Cinema endpoints
            "/cinemas",
            "/cinemas/**",

            //Public Showtime endpoints
            "/showtimes",
            "/showtimes/**",

            //Springdoc / Swagger UI
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/webjars/**",

            //Static
            "/",
            "/index.html",
            "/css/**",
            "/js/**",
            "/images/**"
    };


}


