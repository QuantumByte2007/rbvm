package ma.dxc.app.util;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * The CMDB export doesn't store plain booleans: columns like SOC, Antivirus, Agent_Qualys
 * and Standard_Patching carry values such as "SOC=Yes", "EndpointProtection=Yes" or
 * "Standard_Patching=No". A naive Boolean.parseBoolean() on those always yields false.
 * This adapter extracts the trailing Yes/No token instead.
 */
@Component
public class CmdbFlagAdapterUtil {

    @Named("parseYesNoFlag")
    public boolean parseYesNoFlag(String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        String value = raw.contains("=") ? raw.substring(raw.lastIndexOf('=') + 1) : raw;
        return value.trim().toUpperCase().startsWith("YES");
    }

    // The CMDB "Exception" column holds "None" when there is no risk acceptance, or a free-text
    // justification (Availability, Legacy, ...) when there is one (pfa_formule.pdf, section 3).
    @Named("parseException")
    public boolean parseException(String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        String value = raw.trim();
        return !value.equalsIgnoreCase("None") && !value.equalsIgnoreCase("No");
    }
}
