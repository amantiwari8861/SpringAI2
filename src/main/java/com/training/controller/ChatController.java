package com.training.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

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
    public ChatApiResponse ask(@RequestBody RequestPayload requestPayload) {
        System.out.println(requestPayload.toString());

        String message = chatClient.prompt()
                .user(requestPayload.message())
                .call()
                .content();

        return new ChatApiResponse(true, message);
    }
}

record RequestPayload(String message) {}
record ChatApiResponse(boolean status,String message) {}
