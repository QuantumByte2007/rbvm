package ma.dxc.app.util;

import jakarta.annotation.Nullable;
import ma.dxc.app.entity.Severity;
import org.springframework.stereotype.Component;


@Component
public class QdsAdapter {

    public static Severity classify(Integer qds){
        if(!(qds == null)){
        if(qds<=100 && qds>=90){
            return Severity.CRITICAL;
        } else if (qds>=70 && qds<=89) {
            return Severity.HIGH;
        } else if (qds>=40 && qds<=69) {
            return Severity.MEDIUM;
        }
        return Severity.LOW;
    }
        return null;
    }
}
