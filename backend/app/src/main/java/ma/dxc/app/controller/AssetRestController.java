package ma.dxc.app.controller;

import ma.dxc.app.dto.AssetSummaryDto;
import ma.dxc.app.entity.ReconciliationStatus;
import ma.dxc.app.repository.CmdbCiRepository;
import ma.dxc.app.repository.HostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetRestController {

    private final HostRepository hostRepository;
    private final CmdbCiRepository cmdbCiRepository;

    public AssetRestController(HostRepository hostRepository, CmdbCiRepository cmdbCiRepository) {
        this.hostRepository = hostRepository;
        this.cmdbCiRepository = cmdbCiRepository;
    }

    @GetMapping
    public ResponseEntity<List<AssetSummaryDto>> getAssets() {
        List<AssetSummaryDto> assets = new ArrayList<>(hostRepository.findAllQualysHostWithOptionalCmdb());
        cmdbCiRepository.findAllWithoutHost().forEach(ci -> assets.add(new AssetSummaryDto(
                null, ci.getDns(), ci.getIp(), ci.getEnv(), null, null, null, null, null,
                ReconciliationStatus.SHADOW_CMDB_ONLY)));
        return ResponseEntity.ok(assets);
    }
}
