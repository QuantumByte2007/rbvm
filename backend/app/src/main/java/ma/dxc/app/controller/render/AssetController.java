package ma.dxc.app.controller.render;

import ma.dxc.app.repository.HostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AssetController {
    private final HostRepository hostRepository;

    public AssetController(HostRepository hostRepository) {
        this.hostRepository = hostRepository;
    }

    @GetMapping("/assets")
    public String getAssets(Model model, @RequestHeader(value = "HX-Requests", required = false) String hx){
        List<Object[]> assets = new ArrayList<>();
        assets = hostRepository.findAllQualysHostWithOptionalCmdb();
        model.addAttribute("assets",assets);
        if("true".equals(hx)){
        return "partials/assets-content :: reconciliation-table";}
        else {
            return "pages/assets";
        }
    }
}
