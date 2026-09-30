package devpilot.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devpilot.backend.dto.ChatMessageResponse;
import devpilot.backend.dto.ChatResponse;
import devpilot.backend.dto.ConversationResponse;
import devpilot.backend.dto.RagResponse;
import devpilot.backend.entity.ChatMessage;
import devpilot.backend.entity.Conversation;
import devpilot.backend.entity.MessageRole;
import devpilot.backend.exception.ResourceNotFoundException;
import devpilot.backend.repository.ChatMessageRepository;
import devpilot.backend.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RepositoryService repositoryService;
    private final RagService ragService;

    @Transactional
    public ConversationResponse createConversation(
            UUID userId,
            UUID repositoryId,
            String title) {

        repositoryService.getRepository(repositoryId, userId);

        Conversation conversation =
                Conversation.builder()
                        .userId(userId)
                        .repositoryId(repositoryId)
                        .title(title)
                        .build();

        Conversation saved =
                conversationRepository.save(conversation);

        return toConversationResponse(saved);
    }

    @Transactional
    public ChatResponse ask(
            UUID userId,
            UUID repositoryId,
            UUID conversationId,
            String question) {

        Conversation conversation =
                getConversation(
                        userId,
                        repositoryId,
                        conversationId);

        ChatMessage userMessage =
                ChatMessage.builder()
                        .conversationId(conversation.getId())
                        .role(MessageRole.USER)
                        .content(question)
                        .build();

        chatMessageRepository.save(userMessage);

        RagResponse response =
                ragService.ask(
                        conversation.getRepositoryId(),
                        question);

        ChatMessage assistantMessage =
                ChatMessage.builder()
                        .conversationId(conversation.getId())
                        .role(MessageRole.ASSISTANT)
                        .content(response.answer())
                        .build();

        chatMessageRepository.save(assistantMessage);

        return new ChatResponse(
                conversation.getId(),
                response.answer(),
                response.citations());
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(
            UUID userId,
            UUID repositoryId,
            UUID conversationId) {

        Conversation conversation =
                getConversation(
                        userId,
                        repositoryId,
                        conversationId);

        return chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversation.getId())
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations(
            UUID userId,
            UUID repositoryId) {

        repositoryService.getRepository(
                repositoryId,
                userId);

        return conversationRepository
                .findByUserIdAndRepositoryIdOrderByUpdatedAtDesc(
                        userId,
                        repositoryId)
                .stream()
                .map(this::toConversationResponse)
                .toList();
    }

    private Conversation getConversation(
            UUID userId,
            UUID repositoryId,
            UUID conversationId) {

        Conversation conversation =
                conversationRepository
                        .findByIdAndUserId(
                                conversationId,
                                userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Conversation not found"));

        if (!conversation
                .getRepositoryId()
                .equals(repositoryId)) {

            throw new ResourceNotFoundException(
                    "Conversation not found");
        }

        return conversation;
    }

    public ConversationResponse toConversationResponse(
            Conversation conversation) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getRepositoryId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt());
    }

    public ChatMessageResponse toMessageResponse(
            ChatMessage message) {

        return new ChatMessageResponse(
                message.getId(),
                message.getConversationId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt());
    }
}