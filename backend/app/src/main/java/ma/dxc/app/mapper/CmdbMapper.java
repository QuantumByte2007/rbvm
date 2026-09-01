package ma.dxc.app.mapper;

import ma.dxc.app.dto.CmdbDto;
import ma.dxc.app.entity.CmdbCi;
import ma.dxc.app.entity.Role;
import ma.dxc.app.util.CmdbFlagAdapterUtil;
import ma.dxc.app.util.EnvAdapterUtil;
import ma.dxc.app.util.OsAdapterUtil;
import ma.dxc.app.util.RoleAdapterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OsAdapterUtil.class, RoleAdapterUtil.class, EnvAdapterUtil.class, CmdbFlagAdapterUtil.class})
public interface CmdbMapper {

    @Mapping(target = "agentQualys", source = "agentQualys", qualifiedByName = "parseYesNoFlag")
    @Mapping(target = "soc", source = "soc", qualifiedByName = "parseYesNoFlag")
    @Mapping(target = "antivirus", source = "antivirus", qualifiedByName = "parseYesNoFlag")
    @Mapping(target = "standardPatching", source = "standardPatching", qualifiedByName = "parseYesNoFlag")
    @Mapping(target = "exception", source = "exception", qualifiedByName = "parseException")
    CmdbCi toEntity(CmdbDto cmdbDto);

}
