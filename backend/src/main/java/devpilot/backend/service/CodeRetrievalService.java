package devpilot.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeRetrievalService {

    private final VectorStore vectorStore;

    public List<Document> search(
            UUID repositoryId,
            String query,
            int topK) {

        SearchRequest searchRequest =
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(0.0)
                        .filterExpression(
                                "repositoryId == '" + repositoryId + "'")
                        .build();

        return vectorStore.similaritySearch(searchRequest);
    }
}