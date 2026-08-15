package ma.dxc.app.service;

import ma.dxc.app.entity.Host;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class OsTypeProcessor implements ItemProcessor<Object,Host> {
    @Override
    public @Nullable Host process(Object item) throws Exception {

//        if(item instanceof Host)

        return null;
    }
}
