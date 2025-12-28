package springai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springai.service.EmbeddingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final EmbeddingService embeddingService;
    private final ChatClient chatClient;

    @GetMapping("/search")
    public String searchMovies(@RequestParam(defaultValue = "Tell me about dreams") String query) {
        // 1. Get relevant documents from Postgres
        List<Document> similarDocs = embeddingService.similaritySearch(query);

        // 2. Combine the content of the docs
        String information = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        // 3. Ask the LLM to answer using the found information
        return chatClient.prompt()
                .user(u -> u.text("Answer the question: {query} using only this info: {info}")
                        .param("query", query)
                        .param("info", information))
                .call()
                .content();
    }
}