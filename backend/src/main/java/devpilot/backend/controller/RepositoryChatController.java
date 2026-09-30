package devpilot.backend.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import devpilot.backend.dto.ChatRequest;
import devpilot.backend.dto.RagResponse;
import devpilot.backend.service.RagService;
import devpilot.backend.service.RepositoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/repositories/{repositoryId}/chat")
@RequiredArgsConstructor
@Validated
public class RepositoryChatController {

    private final RepositoryService repositoryService;
    private final RagService ragService;

    @PostMapping
    public RagResponse chat(
            @PathVariable UUID repositoryId,
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        UUID userId =
                UUID.fromString(
                        oauth2User.getAttribute("userId"));

        repositoryService.getRepository(
                repositoryId,
                userId);

        return ragService.ask(
                repositoryId,
                request.question());
    }
}