package ma.dxc.app.service;


import lombok.RequiredArgsConstructor;
import ma.dxc.app.dto.CmdbDto;
import ma.dxc.app.entity.CmdbCi;
import ma.dxc.app.mapper.CmdbMapper;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class CmdbProcessor implements ItemProcessor<CmdbDto, CmdbCi> {

    private final CmdbMapper cmdbMapper;
    @Override
    public @Nullable CmdbCi process(CmdbDto item) throws Exception {
        if (item ==null || !(item instanceof CmdbDto))
            return null;
        return cmdbMapper.toEntity(item);
    }
}
