package ma.dxc.app.controller.render;


import ma.dxc.app.service.RiskConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/riskCalcul")
public class RiskCalcul {

    private final RiskConfigService riskConfigService;

    public RiskCalcul(RiskConfigService riskConfigService) {
        this.riskConfigService = riskConfigService;
    }

    @GetMapping("/weights")
    public String getWeightPage(Model model, @RequestHeader(value = "HX-Request", required = false) String hx) {
        model.addAttribute("config", riskConfigService.getConfig());
        if ("true".equals(hx)) {
            // Bypasses the layout/main wrapper entirely, sending just the raw form
            return "partials/weights-content :: weights";
        }
        // Triggers your th:replace logic to build the full page with the navbar/footer
        return "pages/weights";
    }
    @PostMapping("/submit-weights")
    public String submitWeights(Model model,
                                @RequestParam("wcritical") Double wcritical,
                                @RequestParam("whigh") Double whigh,
                                @RequestParam("wmedium") Double wmedium,
                                @RequestParam("wlow") Double wlow,
                                @RequestHeader(value = "HX-Request", required = false) String hx
                                ){
        riskConfigService.updateSeverityWeights(wcritical, whigh, wmedium, wlow);
        model.addAttribute("message", "weights saved");

        if("true".equals(hx)){
            return "partials/weights-content :: result";
        }
        return "redirect:/riskCalcul/weights";
    }

    @GetMapping("/factors")
    public String getFactorsPage(Model model, @RequestHeader(value = "HX-Request", required = false) String hx) {
        model.addAttribute("config", riskConfigService.getConfig());
        if ("true".equals(hx)) {
            return "partials/factors-content :: factors";
        }
        return "pages/factors";
    }

    @PostMapping("/submit-factors")
    public String submitFactors(Model model,
                                 @RequestParam("iExternal") Double iExternal,
                                 @RequestParam("mSoc") Double mSoc,
                                 @RequestParam("mAv") Double mAv,
                                 @RequestParam("mPatch") Double mPatch,
                                 @RequestHeader(value = "HX-Request", required = false) String hx) {
        riskConfigService.updateFactors(iExternal, mSoc, mAv, mPatch);
        model.addAttribute("message", "factors saved");

        if ("true".equals(hx)) {
            return "partials/factors-content :: result";
        }
        return "redirect:/riskCalcul/factors";
    }

    @GetMapping("/sla")
    public String getSlaPage(Model model, @RequestHeader(value = "HX-Request", required = false) String hx) {
        model.addAttribute("config", riskConfigService.getConfig());
        if ("true".equals(hx)) {
            return "partials/sla-content :: sla";
        }
        return "pages/sla";
    }

    @PostMapping("/submit-sla")
    public String submitSla(Model model,
                             @RequestParam("blackMinArs") Double blackMinArs,
                             @RequestParam("slaBlackDays") Integer slaBlackDays,
                             @RequestParam("redMinArs") Double redMinArs,
                             @RequestParam("slaRedDays") Integer slaRedDays,
                             @RequestParam("orangeMinArs") Double orangeMinArs,
                             @RequestParam("slaOrangeDays") Integer slaOrangeDays,
                             @RequestParam("yellowMinArs") Double yellowMinArs,
                             @RequestParam("slaYellowDays") Integer slaYellowDays,
                             @RequestParam("slaGreenDays") Integer slaGreenDays,
                             @RequestHeader(value = "HX-Request", required = false) String hx) {
        riskConfigService.updateSlaClasses(blackMinArs, slaBlackDays, redMinArs, slaRedDays,
                orangeMinArs, slaOrangeDays, yellowMinArs, slaYellowDays, slaGreenDays);
        model.addAttribute("message", "SLA classes saved");

        if ("true".equals(hx)) {
            return "partials/sla-content :: result";
        }
        return "redirect:/riskCalcul/sla";
    }

}
