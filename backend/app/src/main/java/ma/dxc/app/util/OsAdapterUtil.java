package ma.dxc.app.util;

import ma.dxc.app.entity.Os;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class OsAdapterUtil {

    private final List<Map.Entry<Pattern, Os>> rules = List.of(
            Map.entry(Pattern.compile("(?i)windows"), Os.WINDOWS),
            Map.entry(Pattern.compile("(?i)mac\\s*os|osx|darwin"), Os.MACOS),
            Map.entry(Pattern.compile("(?i)embedded|tiny\\s*core|busybox"), Os.EMBEDDED),
            Map.entry(Pattern.compile("(?i)ubuntu|debian|linux|red\\s*hat|rhel|centos|fedora|suse"), Os.LINUX));

    public Os classify(String rawOs) {
        if (rawOs == null || rawOs.isBlank()) {
            return Os.UNKNOWN;
        }
        for (Map.Entry<Pattern, Os> rule : rules) {
            if (rule.getKey().matcher(rawOs).find()) {
                return rule.getValue();
            }
        }
        return Os.UNKNOWN;
    }
}