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
    // M_Patch (pfa_formule.pdf table 1) applies when the asset is excluded from the
    // standard patch cycle, i.e. Standard_Patching=No in the CMDB export.
    private boolean standardPatching;
    // true when the CMDB "Exception" column holds a reason other than "None" (Risk Acceptance, section 3)
    private boolean exception;
}