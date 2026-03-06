package com.training.config;

import com.training.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
public class AiConfig {

//    @Bean(name = "chatClient")
//    @Profile("ollama")
//    public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
//        return ChatClient.create(chatModel);
//    }
@Value("${serp.api.key}")
private String SERP_API_KEY;
    // Define structured result classes
    public record SearchResult(String title, String link, String snippet) {}
    public record SearchResponse(List<SearchResult> results) {}

    // A sample function to call SerpApi
    Function<SearchRequest, SearchResponse> serpapiSearch = req -> {
        System.out.println("Search tool called "+SERP_API_KEY);
        String query = URLEncoder.encode(req.query(), StandardCharsets.UTF_8);
        String url = "https://serpapi.com/search.json?engine=google&api_key=" + SERP_API_KEY + "&q=" + query;
        System.out.println("Search tool called "+url);
        WebClient webClient = WebClient.create();
        JsonNode json = webClient.get().uri(url)
                .retrieve().bodyToMono(JsonNode.class).block();
        // ✅ Log the full raw response
        System.out.println("🔍 Raw SerpAPI JSON Response:");
        System.out.println(json.toPrettyString());

        List<SearchResult> results = new ArrayList<>();
        // Parse the "organic_results" array
        for (JsonNode item : json.path("organic_results")) {
            String title = item.path("title").asText("");
            String link  = item.path("link").asText("");
            String snippet = item.path("snippet").asText("");
            results.add(new SearchResult(title, link, snippet));
        }
        return new SearchResponse(results);
    };


    @Bean
    public ChatClient chatClient(OpenAiChatModel model,
                                 ChatMemory chatMemory) {

        ToolCallback searchTool = FunctionToolCallback
                .<SearchRequest, SearchResponse>builder("webSearch", serpapiSearch)
                .description("Performs a Google web search using SerpAPI")
                .inputType(SearchRequest.class)
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

    @Bean
    public ImageModel imageModel(OpenAiImageModel openAiImageModel) {
        return openAiImageModel;
    }
}
