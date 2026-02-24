package com.training.config;

import com.training.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class AiConfig {

//    @Bean(name = "chatClient")
//    @Profile("ollama")
//    public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
//        return ChatClient.create(chatModel);
//    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model,
                                 ChatMemory chatMemory,
                                 Function<SearchRequest, SearchResponse> webSearch) {

        var searchTool = FunctionToolCallback.<SearchRequest, SearchResponse>builder("webSearch", webSearch)
                .description("Searches the web with a given query")
                .inputType(SearchRequest.class)  // ✅ REQUIRED
                .build();

        return ChatClient.builder(model)
                .defaultSystem("""
                    You are a professional Java architect.
                    Answer in approximately 20 words.
                     If answering requires external data, use available tools like webSearch.
                """)
                .defaultToolCallbacks(searchTool)
                .defaultTools(new DateTimeTools())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
    public record SearchRequest(String query) {}
    public record SearchResponse(String result) {}

    @Bean
    public Function<SearchRequest, SearchResponse> webSearch() {
        return req -> {
            return new SearchResponse("Result for: " + req.query());
        };
    }
}
