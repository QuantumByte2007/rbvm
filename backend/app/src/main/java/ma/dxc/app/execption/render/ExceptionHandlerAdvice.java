package ma.dxc.app.execption.render;


import jakarta.servlet.http.HttpServletRequest;
import ma.dxc.app.execption.FileNotUploaded;
import ma.dxc.app.execption.FileProcessedException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "ma.dxc.app.controller.render")
public class ExceptionHandlerAdvice {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FileProcessedException.class)
    public String handelFileProcessed(FileProcessedException ex, HttpServletRequest req, Model model){
        Map<String,String> exDetails = new HashMap<>();
        exDetails.put("message",ex.getMessage());
        exDetails.put("status","FAILED");
        model.addAllAttributes(exDetails);
        String hxHeader = req.getHeader("HX-Request");
        if("true".equals(hxHeader)){
            return "fragments/alert :: alert";
        }
        return "pages/error";
    }
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FileNotUploaded.class)
    public String handelFileNotUploaded(FileNotUploaded ex, HttpServletRequest req, Model model){
        Map<String,String> exDetails = new HashMap<>();
        exDetails.put("message",ex.getMessage());
        exDetails.put("status","FAILED");
        model.addAllAttributes(exDetails);
        String hxHeader = req.getHeader("HX-Request");
        if("true".equals(hxHeader)){
            return "fragments/alert :: alert";
        }
        return "pages/error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req, Model model){
        Map<String,String> exDetails = new HashMap<>();
        exDetails.put("message",ex.getMessage());
        exDetails.put("status","FAILED");
        model.addAllAttributes(exDetails);
        String hxHeader = req.getHeader("HX-Request");
        if("true".equals(hxHeader)){
            return "fragments/alert :: alert";
        }
        return "pages/error";
    }

    // Catch-all so a genuinely unexpected exception (e.g. a wrapped batch-job failure
    // from JobService) renders the app's own error page instead of Spring's raw
    // Whitelabel error page.
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, HttpServletRequest req, Model model){
        Map<String,String> exDetails = new HashMap<>();
        exDetails.put("message", ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName());
        exDetails.put("status","FAILED");
        model.addAllAttributes(exDetails);
        String hxHeader = req.getHeader("HX-Request");
        if("true".equals(hxHeader)){
            return "fragments/alert :: alert";
        }
        return "pages/error";
    }

}
