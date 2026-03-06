package com.training.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("api/v1")
public class ChatController {

    private static long count;
    static {
        count=0;
    }

//    @Qualifier("chatClient2")
    private final ChatClient chatClient;
    @Autowired
    private ImageModel imageModel;
    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public String index() {
        return "Application is up and running!";
    }

    @PostMapping("/chat")
    public ChatApiResponse ask(@RequestBody RequestPayload requestPayload) {
//        System.out.println(requestPayload.toString());

        String message = chatClient.prompt()
                .user(requestPayload.message())
                .call()
                .content();
        return new ChatApiResponse(true, message);
    }

    @PostMapping("/generate")
    public ImageApiResponse generate(@RequestBody String prompt) {
        // ✅ Create ImageOptions and set format to b64_json
        ImageOptions options = OpenAiImageOptions.builder()
                .responseFormat("b64_json")
                .N(1)
                .height(1024)
                .width(1024)
                .style("natural")
                .build();

        // ✅ Use that in ImagePrompt
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);

        ImageResponse response = imageModel.call(imagePrompt);
        Image imgResp = response.getResults().getFirst().getOutput();
        String base64 = imgResp.getB64Json(); // should now be populated

        System.out.println("Base64 length: " + (base64 != null ? base64.length() : "null"));

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("img_" + (++count) + ".png"))) {
            byte[] bytes = Base64.getDecoder().decode(base64);
            bos.write(bytes);
            System.out.println("Written: " + bytes.length + " bytes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ImageApiResponse(base64);
    }


//    @PostMapping("/generate")
//    public String generate(@RequestBody String prompt) {
//        // Use the specific implementation (e.g., OpenAiImageOptions)
//        // to ensure the provider recognizes the format request.
//
//
//        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
//        ImageResponse response = imageModel.call(imagePrompt);
//
//        // Safety check: ensure the result exists before accessing
//        if (response.getResults() != null && !response.getResults().isEmpty()) {
//            Image imgResp = response.getResults().get(0).getOutput();
//            String base64 = imgResp.getB64Json();
//
//            System.out.println("Base64 length: " + (base64 != null ? base64.length() : "null"));
//            return base64; // Return the raw string to the client
//        }
//
//        return "Generation failed";
//    }


}

record RequestPayload(String message) {}
record ChatApiResponse(boolean status,String message) {}
record ImageApiResponse(String b64_json){}