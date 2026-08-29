package ma.dxc.app.service;


import ma.dxc.app.entity.Severity;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

@Component
public class Calcul {
    public static final Map<Double,Severity> weights = new HashMap<>();



    public void getWeights(Double w1, Double w2, Double w3, Double w4){
        weights.put(w1,Severity.CRITICAL);
        weights.put(w2,Severity.HIGH);
        weights.put(w3,Severity.MEDIUM);
        weights.put(w4,Severity.LOW);

    }
}
