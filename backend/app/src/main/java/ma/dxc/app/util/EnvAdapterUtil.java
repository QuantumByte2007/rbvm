package ma.dxc.app.util;


import ma.dxc.app.entity.Env;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class EnvAdapterUtil {

    private final List<Map.Entry<Pattern, Env>> rules = List.of(
            Map.entry(Pattern.compile("(?i)staging"),Env.STAGING),
            Map.entry(Pattern.compile("(?i)qa"),Env.QA),
            Map.entry(Pattern.compile("(?i)dev"),Env.DEVELOPMENT),
            Map.entry(Pattern.compile("(?i)test"),Env.TEST),
            Map.entry(Pattern.compile("(?i)prod"),Env.PRODUCTION)
    );

    public Env classify(String rawEnv){
        if(rawEnv == null || rawEnv.isEmpty()){
            return Env.UNKNOWN;
        }
        for (Map.Entry<Pattern,Env> rule:rules){
            if(rule.getKey().matcher(rawEnv).find()){
                return rule.getValue();
            }
        }return Env.UNKNOWN;
    }
}
