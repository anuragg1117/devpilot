package devpilot.backend.dto;

import java.time.Instant;
import java.util.UUID;

import devpilot.backend.entity.MessageRole;

public record ChatMessageResponse(
        UUID id,
        UUID conversationId,
        MessageRole role,
        String content,
        Instant createdAt) {
}