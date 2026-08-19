package ma.dxc.app.controller;


import lombok.AllArgsConstructor;
import ma.dxc.app.service.AiAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent")
public class AiAgentController {

    private final AiAgentService aiAgentService;

    @GetMapping("/ask")
    public ResponseEntity<Flux<String>> askAgent(@RequestParam(defaultValue = "Bonjour") String question){
        return ResponseEntity.ok(aiAgentService.chat(question));
    }
}
