package ma.dxc.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;
@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
public class File{
    @Id
    private UUID id;
    private String ownerId;  // Could be taken from SecurityContext
    private Long size;   //to be calculated
    private String mimeType; // for this one we need TypeCheck() method as we r not trusting user input
    private Instant createdAt;
    private String name;} //same logic as id, size.
