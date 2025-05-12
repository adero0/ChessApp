package org.example.vuejstest.controllers;

import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.models.MoveMade;
import org.example.vuejstest.models.MoveResponse;
import org.example.vuejstest.models.User;
import org.example.vuejstest.repository.UserRepository;
import org.example.vuejstest.services.MatchmakingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        Map<String, Object> result = matchmakingService.joinQueue(user.getId());
        log.info("User {} joined queue or status {}", username, result);
        System.out.println("IM ADDED YAY!");
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

    @PostMapping("/queue/status")
    public ResponseEntity<Map<String, Object>> getQueueStatus() {
        Long userId = getUserId();
        System.out.println("my name is " + userRepository.findById(userId).get().getUsername());
        System.out.println("checking status");
        return ResponseEntity.ok(matchmakingService.getQueueStatusForUser(userId));
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<MoveResponse> submitMove(
            @PathVariable String gameId,
            @RequestBody MoveMade move) {
        String username = getAuthenticatedUsername();
        var thingMove = matchmakingService.processMove(
                gameId,
                getUserId(),
                move
        );
        return ResponseEntity.ok(thingMove);
    }

    @GetMapping("/{gameId}/status")
    public ResponseEntity<Map<String, Object>> getGameStatus(@PathVariable String gameId) {
        String username = getAuthenticatedUsername();
//        log.info("User {} checking status of game {}", username, gameId);
        var thing = matchmakingService.getGameStatus(gameId, getUserId());
        return ResponseEntity.ok(thing);
    }

    @PostMapping("/{gameId}/resign")
    public ResponseEntity<Map<String, Object>> resignGame(
            @PathVariable String gameId) {
        String username = getAuthenticatedUsername();
        log.info("User {} resigning from game {}", username, gameId);
        return ResponseEntity.ok(matchmakingService.resignGame(gameId, getUserId()));
    }

    @PostMapping("/bot_game/join")
    public ResponseEntity<Map<String, Object>> joinBotGame(@RequestBody String color) {
        Long userId = getUserId();
        return ResponseEntity.ok(matchmakingService.joinBotGame(userId, PieceColor.valueOf(color)));
    }

    //once at the start for bot games
    @GetMapping("/bot_game/{gameId}/setup")
    public ResponseEntity<Map<String, Object>> setupBotGame(@PathVariable String gameId) {
        Long userId = getUserId();
        System.out.println("I reached this");
        return ResponseEntity.ok(matchmakingService.getBotGameSetup(gameId, userId));
    }

    @PostMapping("/bot_game/{gameId}/move")
    public ResponseEntity<MoveResponse> moveInBotGame(
            @PathVariable String gameId,
            @RequestBody MoveMade move
    ) {
        Long userId = getUserId();
        return ResponseEntity.ok(matchmakingService.processBotMove(gameId, move));
    }


}