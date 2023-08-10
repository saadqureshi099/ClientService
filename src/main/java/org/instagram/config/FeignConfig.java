package org.instagram.config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@EnableFeignClients
public class FeignConfig {
    /**
     * Intercepts every request made through feign client and sets authorization header extracted from session
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // Retrieve the authorization token from the current session
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authToken = (String) attributes.getRequest().getSession().getAttribute("token");
                if (authToken != null) {
                    template.header("Authorization", "Bearer "+authToken);
                }
            }
        };
    }
}
