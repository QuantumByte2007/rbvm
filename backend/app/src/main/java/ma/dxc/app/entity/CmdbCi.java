package ma.dxc.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
public class CmdbCi {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String ciId;
    private String dns;
    private String ip;
//    @Enumerated(EnumType.STRING)
//    private CmdbStatus cmdbStatus;
    private String vlan;
    @Enumerated(EnumType.STRING)
    private Os os;
    @Enumerated(EnumType.STRING)
    private Env env;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String cmdbOwner;
    private String cmdbSupporter;
    private String applications;
    private String appliOwner;
    private String db;
    private String sw;
    private boolean agentQualys;
    private boolean soc;
    private boolean antivirus;
    private boolean exception;
}