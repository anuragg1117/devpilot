CREATE TABLE code_chunks (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                             repository_id UUID NOT NULL,

                             file_path TEXT NOT NULL,

                             chunk_index INTEGER NOT NULL,

                             content TEXT NOT NULL,

                             created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_code_chunks_repository
                                 FOREIGN KEY (repository_id)
                                     REFERENCES repositories(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT uk_code_chunks_repository_file_chunk
                                 UNIQUE (repository_id, file_path, chunk_index)
);

CREATE INDEX idx_code_chunks_repository_id
    ON code_chunks(repository_id);