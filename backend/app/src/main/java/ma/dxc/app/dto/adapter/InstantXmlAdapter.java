package ma.dxc.app.dto.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.Instant;

/**
 * JAXB cannot bind java.time.Instant out of the box. This adapter converts
 * ISO-8601 UTC strings (e.g. "2019-07-31T21:58:08Z") to/from Instant.
 */
public class InstantXmlAdapter extends XmlAdapter<String, Instant> {

    @Override
    public Instant unmarshal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Instant.parse(value.trim());
    }

    @Override
    public String marshal(Instant instant) {
        return instant == null ? null : instant.toString();
    }
}
