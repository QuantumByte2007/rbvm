package ma.dxc.app.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class FileResponseDto {
    private UUID id;
    private Instant createdAt;
}
