package devpilot.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import devpilot.backend.dto.RagResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RagService {

    private final ChatClient chatClient;
    private final CodeRetrievalService codeRetrievalService;

    public RagResponse ask(
            UUID repositoryId,
            String question) {

        List<Document> documents =
                codeRetrievalService.search(
                        repositoryId,
                        question,
                        5);

        String context = buildContext(documents);

        String prompt = """
                You are DevPilot, an AI assistant that helps developers
                understand and work with GitHub repositories.

                Answer the user's question using the provided repository
                code context.

                Rules:
                1. Use the repository context as the primary source.
                2. Do not invent code, files, classes, methods, or behavior.
                3. If the context is insufficient, clearly say so.
                4. Explain the answer clearly.
                5. Mention relevant file paths when explaining code.

                Repository context:

                %s

                User question:

                %s
                """.formatted(context, question);

        String answer =
                chatClient
                        .prompt()
                        .user(prompt)
                        .call()
                        .content();

        List<RagResponse.Citation> citations =
                documents.stream()
                        .map(document ->
                                new RagResponse.Citation(
                                        (String) document
                                                .getMetadata()
                                                .get("filePath"),
                                        (Integer) document
                                                .getMetadata()
                                                .get("chunkIndex")))
                        .distinct()
                        .toList();

        return new RagResponse(
                answer,
                citations);
    }

    private String buildContext(
            List<Document> documents) {

        if (documents.isEmpty()) {
            return "No relevant repository code was found.";
        }

        StringBuilder context =
                new StringBuilder();

        for (Document document : documents) {

            context.append("\n--- FILE: ")
                    .append(document.getMetadata().get("filePath"))
                    .append(" | CHUNK: ")
                    .append(document.getMetadata().get("chunkIndex"))
                    .append(" ---\n");

            context.append(document.getText());

            context.append("\n");
        }

        return context.toString();
    }
}