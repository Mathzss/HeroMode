package com.example.heromode.core.config;

import org.springframework.context.annotation.Configuration;

// CORS is handled centrally by CorsConfig (Spring Security level).
// Having addCorsMappings here caused 403 because Spring MVC's
// CORS processor was overriding the security filter's CORS headers
// and rejecting origins not in its list (e.g. heromode.com.br).
@Configuration
public class WebConfig {
}