package ma.dxc.app.mapper;


import ma.dxc.app.dto.Hostdto;
import ma.dxc.app.entity.Host;
import ma.dxc.app.util.OsAdapterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OsAdapterUtil.class)
public interface HostMapper {
    Host toEntity(Hostdto hostdto);
}
