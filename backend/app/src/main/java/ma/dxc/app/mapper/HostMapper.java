package ma.dxc.app.mapper;


import ma.dxc.app.dto.Hostdto;
import ma.dxc.app.entity.Host;
import ma.dxc.app.util.OsAdapterUtil;
import ma.dxc.app.util.QdsAdapter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;

@Mapper(componentModel = "spring", uses = {OsAdapterUtil.class, VulnMapper.class})
public interface HostMapper {




    @Mapping(target = "vulns", source = "vulnInfoDtos")
    Host toEntity(Hostdto hostdto);

    @AfterMapping
    default void linkVulns(@MappingTarget Host host) {
        if (host.getVulns() == null) {
            host.setVulns(new ArrayList<>());
            return;
        }
        host.getVulns().forEach(vuln -> {
            vuln.setHost(host);
            vuln.setSeverity(QdsAdapter.classify(vuln.getQds()));
        });


    }


}
