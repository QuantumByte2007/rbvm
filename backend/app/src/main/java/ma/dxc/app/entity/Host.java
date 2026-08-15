package ma.dxc.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "HOST")
@XmlAccessorType(XmlAccessType.FIELD)
public class Host {
    @Id
    private Long id;

    @Column(nullable = false)
    @XmlElement(name = "IP")
    private String ip;

    @XmlElement(name = "DNS")
    private String dns;

    @XmlElement(name = "OPERATING_SYSTEM")
    @Column(name = "operating_system")
    @Enumerated(EnumType.STRING)
    private Os os;
}
