package com.tpe.cinetime.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {

    //app.mail.from
    private String from;

    //app.mail.frontend-url
    private String frontendUrl;

    //app.mail.token-validity-minutes
    private int tokenValidityMinutes = 15;

    //app.mail.reset-path
    private String resetPath = "/reset-password";

    //Tam reset URL'i döndürür: frontendUrl + resetPath + ?token=...
    public String buildResetUrl(String token) {

        return frontendUrl + resetPath + "?token=" + token;
    }
}
