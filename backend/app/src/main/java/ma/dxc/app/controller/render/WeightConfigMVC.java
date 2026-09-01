package ma.dxc.app.controller.render;

import ma.dxc.app.entity.Env;
import ma.dxc.app.entity.RiskClass;
import ma.dxc.app.entity.Role;
import ma.dxc.app.entity.WeightCategory;
import ma.dxc.app.service.RiskWeightConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// One generic controller for Role/Env/MTTR(SlaClass) weights instead of three
// near-identical classes: each is the same shape (one range slider per enum
// value, upserted into RiskWeightConfig), only the enum and title differ.
@Controller
@RequestMapping("/riskCalcul/{category}")
public class WeightConfigMVC {

    private final RiskWeightConfigService riskWeightConfigService;

    public WeightConfigMVC(RiskWeightConfigService riskWeightConfigService) {
        this.riskWeightConfigService = riskWeightConfigService;
    }

    @GetMapping
    public String getWeightPage(@PathVariable String category, Model model,
                                 @RequestHeader(value = "HX-Request", required = false) String hx) {
        WeightCategory weightCategory = resolveCategory(category);
        model.addAttribute("category", category);
        model.addAttribute("title", titleFor(weightCategory));
        model.addAttribute("keys", keysFor(weightCategory));
        model.addAttribute("weights", riskWeightConfigService.getWeights(weightCategory));
        if ("true".equals(hx)) {
            return "partials/weight-category-content :: weightCategory";
        }
        return "pages/weight-category";
    }

    @PostMapping("/submit-weights")
    public String submitWeights(@PathVariable String category,
                                 @RequestParam Map<String, String> allParams,
                                 Model model,
                                 @RequestHeader(value = "HX-Request", required = false) String hx) {
        WeightCategory weightCategory = resolveCategory(category);
        Map<String, Double> weights = new HashMap<>();
        for (String key : keysFor(weightCategory)) {
            String raw = allParams.get(key);
            if (raw != null && !raw.isBlank()) {
                weights.put(key, Double.valueOf(raw));
            }
        }
        riskWeightConfigService.saveWeights(weightCategory, weights);
        model.addAttribute("message", titleFor(weightCategory) + " saved");

        if ("true".equals(hx)) {
            return "partials/weight-category-content :: result";
        }
        return "redirect:/riskCalcul/" + category;
    }

    private WeightCategory resolveCategory(String category) {
        return switch (category) {
            case "role" -> WeightCategory.ROLE;
            case "env" -> WeightCategory.ENV;
            case "mttr" -> WeightCategory.SLA_CLASS;
            default -> throw new IllegalArgumentException("Unknown weight category: " + category);
        };
    }

    private String titleFor(WeightCategory category) {
        return switch (category) {
            case ROLE -> "Role weights (Asset Criticality)";
            case ENV -> "Environment weights (Asset Criticality)";
            case SLA_CLASS -> "MTTR weights per SLA class";
        };
    }

    private List<String> keysFor(WeightCategory category) {
        return switch (category) {
            case ROLE -> java.util.Arrays.stream(Role.values()).map(Enum::name).toList();
            case ENV -> java.util.Arrays.stream(Env.values()).map(Enum::name).toList();
            case SLA_CLASS -> java.util.Arrays.stream(RiskClass.values()).map(Enum::name).toList();
        };
    }
}
