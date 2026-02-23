package com.training.controller;

import com.training.config.AiConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("api/v1")
public class ChatController {

//    @Qualifier("chatClient2")
    private final ChatClient chatClient;
    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public String index() {
        return "Application is up and running!";
    }

    @PostMapping("/chat")
    public ChatApiResponse ask(@RequestBody RequestPayload requestPayload, Function<AiConfig.SearchRequest, AiConfig.SearchResponse> webSearch) {
        String message= chatClient.prompt()
                .user(requestPayload.message())
                .tools(webSearch)
                .call()
                .content();
        return new ChatApiResponse(true, message);
    }
}

record RequestPayload(String message) {}
record ChatApiResponse(boolean status,String message) {}
