package ma.dxc.app.mapper;

import ma.dxc.app.dto.VulnInfoDto;
import ma.dxc.app.entity.VulnInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VulnMapper {

    VulnInfo toEntity(VulnInfoDto vulnInfoDto);
}
