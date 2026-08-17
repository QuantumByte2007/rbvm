package ma.dxc.app.service;

import lombok.RequiredArgsConstructor;
import ma.dxc.app.dto.Hostdto;
import ma.dxc.app.dto.VulnInfoDto;
import ma.dxc.app.entity.Host;
import ma.dxc.app.mapper.HostMapper;
import ma.dxc.app.mapper.VulnMapper;
import ma.dxc.app.util.OsAdapterUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class OsTypeProcessor implements ItemProcessor<Object,Object> {
    private final HostMapper hostMapper;
    private final VulnMapper vulnMapper;
    @Override
    public @Nullable Object process(Object item) throws Exception {

        if(item == null ){
            return null;
        } else if (item instanceof Hostdto) {
            Hostdto hostdto = (Hostdto) item;
            return hostMapper.toEntity(hostdto);

        } else if (item instanceof VulnInfoDto) {
            VulnInfoDto vulnInfoDto = (VulnInfoDto) item;
            return vulnMapper.toEntity(vulnInfoDto);

        }
        return null;


    }
}
