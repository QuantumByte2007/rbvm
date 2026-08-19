package ma.dxc.app.config;

import ma.dxc.app.dto.CmdbDto;
import ma.dxc.app.dto.Hostdto;
import ma.dxc.app.dto.Qiddto;
import ma.dxc.app.dto.VulnInfoDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class OxmConfig {

    @Bean
    Jaxb2Marshaller dynamicMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // Bind the DTO classes that actually map the XML, rather than scanning
        // the whole entity package (which pulls in stray classes and causes
        // JAXB context-build failures).
        marshaller.setClassesToBeBound(Hostdto.class, VulnInfoDto.class, Qiddto.class, CmdbDto.class);
        return marshaller;
    }
}
