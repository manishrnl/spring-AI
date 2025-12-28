package springai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {

    @Value("classpath:faq.pdf")
    Resource faqPdfResource;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public void ingestVectorStore() {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(faqPdfResource);
        List<Document> documents = reader.read();

        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();
        List<Document> chunks = tokenTextSplitter.apply(documents);

        vectorStore.add(chunks);

    }


    public String askAI(String prompt) {
        String template = """
                You are an AI  assistant helping a developer .
                Rules :
                -Use ONLY the information provided in the context
                -You may rephrase , reply and SUMMARISE in natural languages
                -Do NOT introduce new concept or facts
                -If multiple context sections are relevant , combine them into a single explanation.
                -If the answer is not present , say I don't know , I am an AI Agent still learning ....
                
                context:
                {context}
                
                Answer in a friendly , conversational tone .
                """;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(prompt)
                .topK(4)
                .similarityThreshold(0.6)
                .filterExpression("file_name == 'faq.pdf' ")
                .build());

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        PromptTemplate promptTemplate = new PromptTemplate(template);
        String systemPrompt = promptTemplate.render(Map.of("context", context));

        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .advisors(new SimpleLoggerAdvisor())//For full logging
                .call()
                .content();

    }


}
