package springai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmbeddingService {


    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @jakarta.annotation.PostConstruct // This triggers it on startup
    public void init() {
        try {
            ingestDataToVectorStore();
            System.out.println(">>> DATA INGESTED INTO POSTGRES SUCCESSFULLY!");
        } catch (Exception e) {
            System.err.println(">>> INGESTION FAILED: " + e.getMessage());
        }
    }
    public float[] getEmbedding(String text) {
        return embeddingModel.embed(text);
    }

    public void ingestDataToVectorStore() {
        List<Document> movies = List.of(
                new Document("A thief who steals corporate secrets through the use of dream-sharing technology.",
                        Map.of("title", "Inception", "genre", "Sci-Fi", "year", 2010)),

                new Document("A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                        Map.of("title", "Interstellar", "genre", "Sci-Fi", "year", 2014)),

                new Document("A poor yet passionate young man falls in love with a rich young woman, giving her a sense of freedom.",
                        Map.of("title", "The Notebook", "genre", "Romance", "year", 2004))
        );
        vectorStore.add(movies);
        vectorStore.add(springAiDocs());
        // FIX: Explicitly cast to SimpleVectorStore to call save
        if (vectorStore instanceof org.springframework.ai.vectorstore.SimpleVectorStore simpleStore) {
            java.io.File file = new java.io.File("vector-store.json");
            simpleStore.save(file);
            System.out.println(">>> SUCCESS: File created at: " + file.getAbsolutePath());
        } else {
            System.err.println(">>> ERROR: vectorStore is not an instance of SimpleVectorStore!");
        }
    }

    public static List<Document> springAiDocs() {
        return List.of(
                new Document(
                        "Spring AI provides abstractions like ChatClient, ChatModel, and EmbeddingModel to interact with LLMs.",
                        Map.of("topic", "basics")
                ),
                new Document(
                        "A VectorStore is used to persist embeddings and perform similarity search for retrieval augmented generation.",
                        Map.of("topic", "vectorstore")
                ),
                new Document(
                        "Retrieval Augmented Generation combines vector similarity search with prompt augmentation to reduce hallucinations.",
                        Map.of("topic", "rag")
                ),
                new Document(
                        "PgVectorStore stores embeddings inside PostgreSQL using the pgvector extension.",
                        Map.of("topic", "pgvector")
                ),
                new Document(
                        "ChatClient provides a fluent API to send prompts to language models like OpenAI or Ollama.",
                        Map.of("topic", "chat")
                )
        );
    }

    public List<Document> similaritySearch(String text) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(text)
                .topK(3)
                .similarityThreshold(0.3)
                .build());
    }

}
