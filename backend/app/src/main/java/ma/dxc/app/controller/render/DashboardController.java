package ma.dxc.app.controller.render;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String getDashboard(@RequestHeader(value = "HX-Request", required = false) String hx) {
        if ("true".equals(hx)) {
            return "partials/dashboard-content :: dashboard";
        }
        return "pages/dashboard";
    }
}
