package com.example.heromode.core.config;

import org.springframework.context.annotation.Configuration;

// CORS is handled centrally by CorsConfig (Spring Security level).
// Having addCorsMappings here previously caused 403: Spring MVC's CORS
// processor ran AFTER Spring Security's CorsFilter and rejected any
// origin not in its localhost-only list — overriding the correct
// CORS headers already set by the security filter.
@Configuration
public class WebConfig {
}
