package ma.dxc.app.mapper;

import ma.dxc.app.dto.CmdbDto;
import ma.dxc.app.entity.CmdbCi;
import ma.dxc.app.entity.Role;
import ma.dxc.app.util.EnvAdapterUtil;
import ma.dxc.app.util.OsAdapterUtil;
import ma.dxc.app.util.RoleAdapterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OsAdapterUtil.class, RoleAdapterUtil.class, EnvAdapterUtil.class})
public interface CmdbMapper {


    CmdbCi toEntity(CmdbDto cmdbDto);

}
