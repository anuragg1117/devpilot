package devpilot.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devpilot.backend.entity.Conversation;

public interface ConversationRepository
        extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByIdAndUserId(
            UUID id,
            UUID userId);

    List<Conversation> findByUserIdAndRepositoryIdOrderByUpdatedAtDesc(
            UUID userId,
            UUID repositoryId);
}