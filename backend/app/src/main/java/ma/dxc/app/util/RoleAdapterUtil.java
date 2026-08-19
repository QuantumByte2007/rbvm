package ma.dxc.app.util;

import ma.dxc.app.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class RoleAdapterUtil {

    private final List<Map.Entry<Pattern, Role>> rules = List.of(
            Map.entry(Pattern.compile("(?i)db|database"),Role.DATABASE_SERVER),
            Map.entry(Pattern.compile("(?i)router"),Role.IP_ROUTER),
            Map.entry(Pattern.compile("(?i)switch"), Role.IP_SWITCH),
            Map.entry(Pattern.compile("(?i)linux.*vm"),Role.VS_LINUX_VMWARE),
            Map.entry(Pattern.compile("(?i)windows.*vm"),Role.VS_WINDOWS_VMWARE)

    );
    public Role classify(String rawRole){
        if(rawRole == null ||rawRole.isEmpty()){return Role.UNKNOWN;}
        for (Map.Entry<Pattern,Role> rule: rules){
            if(rule.getKey().matcher(rawRole).find()){return rule.getValue();}

        }
        return Role.UNKNOWN;
    }
}
