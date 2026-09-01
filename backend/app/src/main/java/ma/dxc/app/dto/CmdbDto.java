package ma.dxc.app.dto;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import ma.dxc.app.entity.Os;
@Data
@XmlRootElement(name = "row")
@XmlAccessorType(XmlAccessType.FIELD)
public class CmdbDto {
    @XmlElement(name = "CI")
    private String ciId;
    @XmlElement(name = "DNS")
    private String dns;
    @XmlElement(name = "IP")
    private String ip;

//    private CmdbStatus cmdbStatus;
    @XmlElement(name = "VLAN")
    private String vlan;
    @XmlElement(name = "OS")
    private String os;
    @XmlElement(name = "ENV")
    private String env;
    @XmlElement(name = "ROLE")
    private String role;
    @XmlElement(name = "CMDB_Owner")
    private String cmdbOwner;
    @XmlElement(name = "CMDB_Supporter")
    private String cmdbSupporter;
    @XmlElement(name = "Applications")
    private String applications;
    @XmlElement(name = "Appli_Owner")
    private String appliOwner;
    @XmlElement(name = "DB")
    private String db;
    @XmlElement(name = "SW")
    private String sw;
    @XmlElement(name = "Agent_Qualys")
    private String agentQualys;
    @XmlElement(name = "SOC")
    private String soc;
    @XmlElement(name = "Antivirus")
    private String antivirus;
    @XmlElement(name = "Standard_Patching")
    private String standardPatching;
    @XmlElement(name = "Exception")
    private String exception;
}
