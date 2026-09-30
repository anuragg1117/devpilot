CREATE TABLE conversations (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               repository_id UUID NOT NULL,
                               title VARCHAR(255),
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                               CONSTRAINT fk_conversation_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id),

                               CONSTRAINT fk_conversation_repository
                                   FOREIGN KEY (repository_id)
                                       REFERENCES repositories(id)
);

CREATE INDEX idx_conversations_user_id
    ON conversations(user_id);

CREATE INDEX idx_conversations_repository_id
    ON conversations(repository_id);

CREATE TABLE chat_messages (
                               id UUID PRIMARY KEY,
                               conversation_id UUID NOT NULL,
                               role VARCHAR(20) NOT NULL,
                               content TEXT NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                               CONSTRAINT fk_chat_message_conversation
                                   FOREIGN KEY (conversation_id)
                                       REFERENCES conversations(id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_chat_messages_conversation_id
    ON chat_messages(conversation_id);

CREATE INDEX idx_chat_messages_created_at
    ON chat_messages(created_at);