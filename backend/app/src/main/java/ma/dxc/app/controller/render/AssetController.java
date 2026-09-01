package ma.dxc.app.controller.render;

import ma.dxc.app.dto.AssetSummaryDto;
import ma.dxc.app.entity.CmdbCi;
import ma.dxc.app.entity.Host;
import ma.dxc.app.entity.ReconciliationStatus;
import ma.dxc.app.entity.RiskClass;
import ma.dxc.app.entity.SlaStatus;
import ma.dxc.app.entity.WeightCategory;
import ma.dxc.app.repository.CmdbCiRepository;
import ma.dxc.app.repository.HostRepository;
import ma.dxc.app.service.RiskCalculationService;
import ma.dxc.app.service.RiskWeightConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AssetController {
    private final HostRepository hostRepository;
    private final CmdbCiRepository cmdbCiRepository;
    private final RiskCalculationService riskCalculationService;
    private final RiskWeightConfigService riskWeightConfigService;

    public AssetController(HostRepository hostRepository, CmdbCiRepository cmdbCiRepository,
                            RiskCalculationService riskCalculationService,
                            RiskWeightConfigService riskWeightConfigService) {
        this.hostRepository = hostRepository;
        this.cmdbCiRepository = cmdbCiRepository;
        this.riskCalculationService = riskCalculationService;
        this.riskWeightConfigService = riskWeightConfigService;
    }

    @GetMapping("/assets")
    public String getAssets(Model model, @RequestHeader(value = "HX-Request", required = false) String hx){
        List<AssetSummaryDto> assets = allAssetsWithShadowIt();
        model.addAttribute("assets", assets);
        model.addAttribute("weightsConfigured", isWeightsConfigured());
        model.addAttribute("totalCount", assets.size());
        model.addAttribute("violatedCount", assets.stream().filter(a -> a.slaStatus() == SlaStatus.VIOLATED).count());
        model.addAttribute("criticalCount", assets.stream()
                .filter(a -> a.riskClass() == RiskClass.BLACK || a.riskClass() == RiskClass.RED).count());
        model.addAttribute("shadowItCount", assets.stream().filter(AssetSummaryDto::isShadowIt).count());
        if("true".equals(hx)){
        return "partials/assets-content :: reconciliation-table";}
        else {
            return "pages/assets";
        }
    }

    // Both reconciliation gaps count as Shadow IT: a Qualys host with no CMDB entry, or
    // a CMDB entry nobody has scanned.
    private List<AssetSummaryDto> allAssetsWithShadowIt() {
        List<AssetSummaryDto> assets = new ArrayList<>(hostRepository.findAllQualysHostWithOptionalCmdb());
        cmdbCiRepository.findAllWithoutHost().forEach(ci -> assets.add(new AssetSummaryDto(
                null, ci.getDns(), ci.getIp(), ci.getEnv(), null, null, null, null, null,
                ReconciliationStatus.SHADOW_CMDB_ONLY)));
        return assets;
    }

    @GetMapping("/assets/{id}")
    public String getAssetDetail(@PathVariable Long id, Model model,
                                  @RequestHeader(value = "HX-Request", required = false) String hx) {
        Host host = hostRepository.findByIdWithVulns(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown asset id " + id));
        CmdbCi cmdbCi = cmdbCiRepository.findByIp(host.getIp()).orElse(null);

        model.addAttribute("host", host);
        model.addAttribute("cmdbCi", cmdbCi);
        model.addAttribute("weightsConfigured", isWeightsConfigured());
        if (cmdbCi != null && isWeightsConfigured()) {
            model.addAttribute("breakdown", riskCalculationService.explain(host, cmdbCi));
        }

        if ("true".equals(hx)) {
            return "partials/asset-detail-content :: assetDetail";
        }
        return "pages/asset-detail";
    }

    private boolean isWeightsConfigured() {
        return riskWeightConfigService.isConfigured(WeightCategory.ROLE)
                && riskWeightConfigService.isConfigured(WeightCategory.ENV);
    }
}
