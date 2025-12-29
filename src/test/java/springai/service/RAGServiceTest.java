package springai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RAGServiceTest {

    @Autowired
    private RAGService ragService;

    @Test
    void askAI() {
        String response = ragService.askAI("how to connect to discord account");
        System.out.println( response);
    }

    @Test
    void testIngest() {
        ragService.ingestVectorStore();
    }

    @Test
    void testAskAIWithAdvisor() {
        String response = ragService.askAiWithAdvisor("my player is not working", "1");
        System.out.println( response);
    }
}
