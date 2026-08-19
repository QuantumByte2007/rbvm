package ma.dxc.app.service;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import ma.dxc.app.dto.HostSummaryDto;
import ma.dxc.app.repository.HostRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiAgentTool{
    private final HostRepository hostRepository;

    @Tool("List all hosts stored in the database")
    List<HostSummaryDto> getAllHosts(){
        return hostRepository.findAll().stream()
                .map(host -> new HostSummaryDto(host.getId(), host.getIp(), host.getDns(), host.getOs()))
                .toList();
    }
}
