package ma.dxc.app.controller.render;


import ma.dxc.app.entity.Severity;
import ma.dxc.app.service.Calcul;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/riskCalcul")
public class RiskCalcul {



    @GetMapping("/weights")
    public String getWeightPage(@RequestHeader(value = "HX-Request", required = false) String hx) {
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
        model.addAttribute("message", "weights saved");

        if("true".equals(hx)){
        return "partials/weights-content :: result";}
        else {throw new RuntimeException();}

    }


}
