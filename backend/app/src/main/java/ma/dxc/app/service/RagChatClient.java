package ma.dxc.app.service;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class RagChatClient {
    private final ChatModel chatModel;

    public RagChatClient(ChatModel chatModel){
        this.chatModel = chatModel;
    }

    public String ask(String question){
        return chatModel.chat(question);
    }
}
