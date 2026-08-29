package ma.dxc.app.service;

import lombok.RequiredArgsConstructor;
import ma.dxc.app.dto.Hostdto;
import ma.dxc.app.entity.Host;
import ma.dxc.app.mapper.HostMapper;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class OsTypeProcessor implements ItemProcessor<Hostdto, Host> {
    private final HostMapper hostMapper;

    //completableFuture future = CompletableFuture.supplyAsync(()->)

    @Override
    public @Nullable Host process(Hostdto item) {
        return hostMapper.toEntity(item);
    }
}
