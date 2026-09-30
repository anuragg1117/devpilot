package devpilot.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import devpilot.backend.dto.ChatMessageResponse;
import devpilot.backend.dto.ChatRequest;
import devpilot.backend.dto.ChatResponse;
import devpilot.backend.dto.ConversationResponse;
import devpilot.backend.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/repositories/{repositoryId}/conversations")
@RequiredArgsConstructor
@Validated
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ConversationResponse createConversation(
            @PathVariable UUID repositoryId,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        UUID userId =
                UUID.fromString(
                        oauth2User.getAttribute("userId"));

        return conversationService.createConversation(
                userId,
                repositoryId,
                "New conversation");
    }

    @GetMapping
    public List<ConversationResponse> getConversations(
            @PathVariable UUID repositoryId,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        UUID userId =
                UUID.fromString(
                        oauth2User.getAttribute("userId"));

        return conversationService.getConversations(
                userId,
                repositoryId);
    }

    @PostMapping("/{conversationId}/messages")
    public ChatResponse ask(
            @PathVariable UUID repositoryId,
            @PathVariable UUID conversationId,
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        UUID userId =
                UUID.fromString(
                        oauth2User.getAttribute("userId"));

        return conversationService.ask(
                userId,
                repositoryId,
                conversationId,
                request.question());
    }

    @GetMapping("/{conversationId}/messages")
    public List<ChatMessageResponse> getMessages(
            @PathVariable UUID repositoryId,
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        UUID userId =
                UUID.fromString(
                        oauth2User.getAttribute("userId"));

        return conversationService.getMessages(
                userId,
                repositoryId,
                conversationId);
    }
}