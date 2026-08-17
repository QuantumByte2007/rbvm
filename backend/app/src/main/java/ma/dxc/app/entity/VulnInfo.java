package ma.dxc.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VulnInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @OneToOne(cascade = CascadeType.ALL)
//    private Qiddto qid;
    private String type;
    private Instant firstFound;
    private Instant lastFound;
    private String Status;
    private int timesFound;
    private String protocol;
    private long port;
    private String service;
    //    @XmlElement(name = "SSL")
//    @Column(name = "isssl")
//    private boolean ssl;
    @ManyToOne
    private Host host;
}