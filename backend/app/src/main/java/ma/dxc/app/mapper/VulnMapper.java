package ma.dxc.app.mapper;

import ma.dxc.app.dto.VulnInfoDto;
import ma.dxc.app.entity.VulnInfo;
import ma.dxc.app.util.QdsAdapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {QdsAdapter.class})
public interface VulnMapper {
    @Mapping(source = "qds", target = "qds")
    @Mapping(target = "qidValue", source = "qid.qid")
    VulnInfo toEntity(VulnInfoDto vulnInfoDto);
}
