package springai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;

@Slf4j
public class TokenUsageAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        long startTime = System.currentTimeMillis();
        // 1. Pass the request down to the chaim (to the LLM)
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        //2. Extract the actual LLM Response
        ChatResponse chatResponse = chatClientResponse.chatResponse();

        //3. Log the token usage details

        if (chatResponse != null && chatResponse.getMetadata().getUsage() != null) {
            var usage = chatResponse.getMetadata().getUsage();
            long currentTime = (System.currentTimeMillis() - startTime);
            long duration = currentTime != 0 ? currentTime / 1000 : 0;
            log.info("Token Usage - Input ={} | Output ={} | Total ={} | Time ={} seconds",
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    usage.getTotalTokens(),
                    duration);
        }


        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "ChatClientResponse";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
