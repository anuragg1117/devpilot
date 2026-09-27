CREATE TABLE repositories (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                              user_id UUID NOT NULL,

                              github_repo_id BIGINT NOT NULL,

                              owner VARCHAR(100) NOT NULL,

                              name VARCHAR(200) NOT NULL,

                              full_name VARCHAR(300) NOT NULL,

                              is_private BOOLEAN NOT NULL DEFAULT FALSE,

                              default_branch VARCHAR(100) NOT NULL,

                              language VARCHAR(100),

                              html_url VARCHAR(500),

                              description TEXT,

                              index_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

                              indexed_at TIMESTAMPTZ,

                              chunk_count INTEGER NOT NULL DEFAULT 0,

                              files_total INTEGER NOT NULL DEFAULT 0,

                              files_processed INTEGER NOT NULL DEFAULT 0,

                              error_message TEXT,

                              created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT uk_repositories_user_github_repo
                                  UNIQUE (user_id, github_repo_id),

                              CONSTRAINT fk_repositories_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE
);

CREATE INDEX idx_repositories_user_id
    ON repositories(user_id);

CREATE INDEX idx_repositories_index_status
    ON repositories(index_status);