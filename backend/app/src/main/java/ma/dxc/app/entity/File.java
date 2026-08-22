package ma.dxc.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;
@Entity
@Data
@AllArgsConstructor @NoArgsConstructor

@Table(indexes = {@Index(name = "idx_hash", columnList = "hash"),@Index(name = "idx_name", columnList = "name")})
public class File{
    @Id
    private UUID id;
    private String ownerId;  // Could be taken from SecurityContext
    private Long size;   //to be calculated
    private String mimeType; // for this one we need TypeCheck() method as we r not trusting user input
    private Instant createdAt;
    private String name;
    private String hash;
    private boolean isProcessed = false;

    public void setProcessed() {
        isProcessed = true;
    }
} //same logic as id, size.
