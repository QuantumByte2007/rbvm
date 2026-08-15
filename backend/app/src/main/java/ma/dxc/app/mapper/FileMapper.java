package ma.dxc.app.mapper;


import ma.dxc.app.dto.FileRequestDto;
import ma.dxc.app.dto.FileResponseDto;
import ma.dxc.app.entity.File;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileResponseDto toDto(File file);
    File toEntity(FileRequestDto fileRequestDto);
}
