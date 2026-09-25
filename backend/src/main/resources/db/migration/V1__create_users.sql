CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       github_id BIGINT NOT NULL UNIQUE,
                       github_username VARCHAR(100) NOT NULL,
                       display_name VARCHAR(200) NOT NULL,
                       avatar_url VARCHAR(500),
                       access_token TEXT NOT NULL,
                       token_scopes VARCHAR(500),
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);