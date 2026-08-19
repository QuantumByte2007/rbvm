package ma.dxc.app.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "HOST")
@XmlAccessorType(XmlAccessType.FIELD)
public class Hostdto {
    private Long id;
    @XmlElement(name = "IP")
    private String ip;
    @XmlElement(name= "DNS")
    private String dns;
    @XmlElement(name= "OPERATING_SYSTEM")
    private String os;
    @XmlElementWrapper(name = "VULN_INFO_LIST")
    @XmlElement(name = "VULN_INFO")
    private List<VulnInfoDto> vulnInfoDtos;
}


