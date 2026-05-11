package com.example.heromode.shared;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public health endpoint used to verify deployment status and prove
 * that the latest build is actually running on Render.
 *
 * If GET /health returns "HEROMODE_OK_v5" the new build is live.
 * If it returns 403, the deploy is still serving an old image.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "HEROMODE_OK_v5";
    }
}
