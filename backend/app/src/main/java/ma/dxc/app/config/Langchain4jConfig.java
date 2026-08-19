package ma.dxc.app.config;

import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Langchain4jConfig {

    @Bean
    ChatModel chatModel(
            @Value("${langchain4j.ollama.chat-model.base-url}") String baseUrl,
            @Value("${langchain4j.ollama.chat-model.model-name}") String modelName) {
        // Le client HTTP est impose explicitement : le pont spring-restclient reste sur le
        // classpath et le ServiceLoader pourrait le choisir, or il est incompatible Boot 4.
        return OllamaChatModel.builder()
                .httpClientBuilder(new JdkHttpClientBuilder())
                .baseUrl(baseUrl)
                .modelName(modelName)
                .build();
    }


    @Bean
    StreamingChatModel streamingChatModel(
            @Value("${langchain4j.ollama.chat-model.base-url}") String baseUrl,
            @Value("${langchain4j.ollama.chat-model.model-name}") String modelName) {
        // Le client HTTP est impose explicitement : le pont spring-restclient reste sur le
        // classpath et le ServiceLoader pourrait le choisir, or il est incompatible Boot 4.
        return OllamaStreamingChatModel.builder()
                .httpClientBuilder(new JdkHttpClientBuilder())
                .baseUrl(baseUrl)
                .modelName(modelName)
                .build();
    }
}
