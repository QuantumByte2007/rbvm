package ma.dxc.app.controller;

import ma.dxc.app.entity.RiskConfig;
import ma.dxc.app.service.RiskConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/riskCalcul")
public class RiskConfigController {

    private final RiskConfigService riskConfigService;

    public RiskConfigController(RiskConfigService riskConfigService) {
        this.riskConfigService = riskConfigService;
    }

    @GetMapping("/severity")
    public ResponseEntity<RiskConfig> getSeverityWeights() {
        return ResponseEntity.ok(riskConfigService.getConfig());
    }

    @PostMapping("/severity")
    public ResponseEntity<RiskConfig> submitSeverityWeights(@RequestBody SeverityWeightsRequest request) {
        return ResponseEntity.ok(riskConfigService.updateSeverityWeights(
                request.wCritical(), request.wHigh(), request.wMedium(), request.wLow()));
    }

    @GetMapping("/factors")
    public ResponseEntity<RiskConfig> getFactors() {
        return ResponseEntity.ok(riskConfigService.getConfig());
    }

    @PostMapping("/factors")
    public ResponseEntity<RiskConfig> submitFactors(@RequestBody FactorsRequest request) {
        return ResponseEntity.ok(riskConfigService.updateFactors(
                request.iExternal(), request.mSoc(), request.mAv(), request.mPatch()));
    }

    public record SeverityWeightsRequest(Double wCritical, Double wHigh, Double wMedium, Double wLow) {
    }

    public record FactorsRequest(Double iExternal, Double mSoc, Double mAv, Double mPatch) {
    }
}
