package ma.dxc.app;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    @Pattern(regexp = ".*[/ \\\\]$",message = "Must end with a forward slash")
    @NotBlank
    String basePath;
    Set<String> mimeType = Set.of(
            "text/csv",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "image/jpeg",
            "image/png",
            "application/pdf",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/xml"
    );




}
