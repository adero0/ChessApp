package org.example.vuejstest.controllers;

import org.example.vuejstest.models.Message;
import org.example.vuejstest.models.User;
import org.example.vuejstest.repository.UserRepository;
import org.example.vuejstest.services.MatchmakingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class MatchmakingController {
    private static final Logger log = LoggerFactory.getLogger(MatchmakingController.class);

    private final MatchmakingService matchmakingService;

    private final UserRepository userRepository;

    public MatchmakingController(MatchmakingService matchmakingService, UserRepository userRepository) {
        this.matchmakingService = matchmakingService;
        this.userRepository = userRepository;
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Long getUserId() {
        return userRepository.findByUsername(getAuthenticatedUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @PostMapping("/queue/join")
    public ResponseEntity<?> joinQueue() {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("User {} joining queue", username);
        Map<String, Object> result = matchmakingService.joinQueue(user.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/queue/leave")
    public ResponseEntity<Void> leaveQueue() {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        matchmakingService.leaveQueue(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<Map<String, Object>> submitMove(
            @PathVariable String gameId,
            @RequestBody Map<String, Integer> move) {
        String username = getAuthenticatedUsername();
        log.info("User {} submitting move in game {}", username, gameId);
        return ResponseEntity.ok(matchmakingService.processMove(
                gameId,
                getUserId(),
                move.get("from"),
                move.get("to")
        ));
    }

    @GetMapping("/{gameId}/status")
    public ResponseEntity<Map<String, Object>> getGameStatus(
            @PathVariable String gameId) {
        String username = getAuthenticatedUsername();
        log.info("User {} checking status of game {}", username, gameId);
        return ResponseEntity.ok(matchmakingService.getGameStatus(gameId, getUserId()));
    }

    @PostMapping("/{gameId}/resign")
    public ResponseEntity<Map<String, Object>> resignGame(
            @PathVariable String gameId) {
        String username = getAuthenticatedUsername();
        log.info("User {} resigning from game {}", username, gameId);
        return ResponseEntity.ok(matchmakingService.resignGame(gameId, getUserId()));
    }

}