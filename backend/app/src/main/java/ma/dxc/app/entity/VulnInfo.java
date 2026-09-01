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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
//    @OneToOne(cascade = CascadeType.ALL)
//    private Qiddto qid;
    private String qidValue;
    private String type;
    private Instant firstFound;
    private Instant lastFound;
    private Instant lastfixed;
    private String Status;
    private int timesFound;
    private String protocol;
    private long port;
    private String service;
    private Integer qds;
    @Enumerated(value = EnumType.STRING)
    private Severity severity;
    //    @XmlElement(name = "SSL")
//    @Column(name = "isssl")
//    private boolean ssl;
    @ManyToOne
    private Host host;
}