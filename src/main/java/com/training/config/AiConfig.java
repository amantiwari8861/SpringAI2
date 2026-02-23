package com.training.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
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
                                 ChatMemory chatMemory,Function<SearchRequest, SearchResponse> webSearch) {

        return ChatClient.builder(model)
                .defaultSystem("""
                You are a professional Java architect.
                Answer in approximately 20 words.
            """)
                .defaultTools(webSearch)   // ✅ Spring AI 2.x way
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
    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }
}
