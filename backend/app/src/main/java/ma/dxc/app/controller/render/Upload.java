package ma.dxc.app.controller.render;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Upload {

    @GetMapping("/upload")
    public String uploadPage(){
        return "upload";
    }
    @GetMapping("/joblaunch")
    public String getJobPage(){
        return "pages/DataSources";
    }
}
