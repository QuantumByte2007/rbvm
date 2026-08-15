package ma.dxc.app.dto;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ma.dxc.app.dto.adapter.InstantXmlAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@XmlRootElement(name = "VULN_INFO")
@XmlAccessorType(XmlAccessType.FIELD)
public class VulnInfoDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @XmlElement(name = "QID")
    @OneToOne
    private Qiddto qid;
    @XmlElement(name = "TYPE")
    private String type;
    @XmlElement(name = "FIRST_FOUND")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    private Instant firstFound;
    @XmlElement(name = "LAST_FOUND")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    private Instant lastFound;
    @XmlElement(name = "VULN_STATUS")
    private String Status;
    @XmlElement(name = "TIMES_FOUND")
    private int timesFound;
    @XmlElement(name = "PROTOCOL")
    private String protocol;
    @XmlElement(name = "PORT")
    private long port;
    @XmlElement(name = "SERVICE")
    private String service;
    @XmlElement(name = "SSL")
    private boolean ssl;
    @ManyToOne
    @JoinColumn(name = "host_id")
    private Hostdto host;

}
