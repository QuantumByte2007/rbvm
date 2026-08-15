package ma.dxc.app.dto;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "HOST")
@XmlAccessorType(XmlAccessType.FIELD)
public class Hostdto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_id")
    private Long id;
    @XmlElement(name = "IP")
    private String ip;
    @XmlElement(name= "DNS")
    private String dns;
    @XmlElement(name= "OPERATING_SYSTEM")
    private String os;
    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    @XmlElementWrapper(name = "VULN_INFO_LIST")
    private List<VulnInfoDto> vulnInfoDtos;
}


