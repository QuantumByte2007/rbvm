package ma.dxc.app.service;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

@AiService
public interface AiAgentService {
    @SystemMessage("""
            You are an assistant with access to tools that query a live database.
            When a question can be answered by a tool, you MUST invoke the tool.
            Never describe, explain or write out the call - actually perform it.
            Never output code.""")
    Flux<String> chat(String question);

}
