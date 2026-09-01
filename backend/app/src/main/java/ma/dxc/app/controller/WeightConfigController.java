package ma.dxc.app.controller;

import ma.dxc.app.entity.Env;
import ma.dxc.app.entity.RiskClass;
import ma.dxc.app.entity.Role;
import ma.dxc.app.entity.WeightCategory;
import ma.dxc.app.service.RiskWeightConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/riskCalcul/{category}")
public class WeightConfigController {

    private final RiskWeightConfigService riskWeightConfigService;

    public WeightConfigController(RiskWeightConfigService riskWeightConfigService) {
        this.riskWeightConfigService = riskWeightConfigService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Double>> getWeights(@PathVariable String category) {
        return ResponseEntity.ok(riskWeightConfigService.getWeights(resolveCategory(category)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Double>> submitWeights(@PathVariable String category,
                                                               @RequestBody Map<String, Double> weights) {
        WeightCategory weightCategory = resolveCategory(category);
        riskWeightConfigService.saveWeights(weightCategory, weights);
        return ResponseEntity.ok(riskWeightConfigService.getWeights(weightCategory));
    }

    private WeightCategory resolveCategory(String category) {
        return switch (category) {
            case "role" -> WeightCategory.ROLE;
            case "env" -> WeightCategory.ENV;
            case "mttr" -> WeightCategory.SLA_CLASS;
            default -> throw new IllegalArgumentException("Unknown weight category: " + category);
        };
    }
}
