package ma.dxc.app.controller;


import lombok.AllArgsConstructor;
import ma.dxc.app.service.RagChatClient;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class RagChatController {

    private final RagChatClient ragChatClient;

    @GetMapping(value = "/ask", produces = MediaType.APPLICATION_JSON_VALUE)
    public String ask(@RequestParam String question){
        return ragChatClient.ask(question);
    }



}
