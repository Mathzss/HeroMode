package com.example.heromode.features.missions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/missions")
//@CrossOrigin(origins = "*")
public class MissionController {

    private final MissionService service;

    public MissionController(MissionService service) {
        this.service = service;
    }

    @PostMapping
    public Mission add(@RequestBody MissionRequest request) {
        return service.createMission(request);
    }

    @GetMapping
    public List<MissionLog> listToday(@AuthenticationPrincipal String email) {
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder
                        .getContext().getAuthentication();
        Long userId = (Long) auth.getCredentials();
        return service.getTodayLogsByUserId(userId);

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteMission(id);
    }

    @PatchMapping("/{logId}/complete")
    public ResponseEntity<Integer> completeLog(@PathVariable Long logId){
        int xpGained = service.completeLog(logId);
        return ResponseEntity.ok(xpGained);
    }
}