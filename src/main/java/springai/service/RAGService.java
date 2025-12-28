package springai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RAGService {

    @Value("classpath:faq.pdf")
    Resource faqPdfResource;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public void ingestVectorStore() {
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(faqPdfResource);
        List<Document> documents = pagePdfDocumentReader.read();

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(200, 50, 5, 10000, true);
        List<Document> chunks = tokenTextSplitter.apply(documents);

//        vectorStore.add(chunks);

    }

    public String askAI(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }



}
