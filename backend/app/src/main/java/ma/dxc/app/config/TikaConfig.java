package ma.dxc.app.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaConfig {


    @Bean
    public Tika getTikaObj(){
        return new Tika();
    }
}
