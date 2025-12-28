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
        System.out.println("AI Response: {}" + response);
    }

    @Test
    void testIngest(){
      ragService.ingestVectorStore();
    }
}
