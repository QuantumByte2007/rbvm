package ma.dxc.app.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class RagChatClient {
    private ChatClient chatClient;

    public RagChatClient(ChatClient.Builder chatclient){
        this.chatClient = chatclient.build();

    }

    public String ask(String question){
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
