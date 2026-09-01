package ma.dxc.app.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dxc.app.dto.VulnInfoDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Host {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String ip;

    @XmlElement(name = "DNS")
    private String dns;


    @Column(name = "operating_system")
    @Enumerated(EnumType.STRING)
    private Os os;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VulnInfo> vulns = new ArrayList<>();

    // ACS (Asset Criticality Score), computed by RiskCalculationService as
    // rescale_1_5(avg(roleWeight[cmdbCi.role], envWeight[cmdbCi.env])) - see RiskWeightConfig.
    private Double acs;

    // Computed by RiskCalculationService from pfa_formule.pdf eq. 1
    private Double ars;

    @Enumerated(EnumType.STRING)
    private RiskClass riskClass;

    @Enumerated(EnumType.STRING)
    private SlaStatus slaStatus;

    private Instant slaDueDate;

    // Age (in days) of the host's oldest still-open vulnerability as of the last
    // recompute - paired with ars on the dashboard's SLA-compliance scatter chart.
    private Double ageInDays;

    private Instant lastCalculatedAt;
}
