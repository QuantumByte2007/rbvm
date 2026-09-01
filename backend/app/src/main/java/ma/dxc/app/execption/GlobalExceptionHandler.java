package ma.dxc.app.execption;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Map<String,String>> handelValidation(ConstraintViolationException exception){
        Map<String,String> exDetails = new HashMap<>();
        exDetails.put("details", exception.getLocalizedMessage());
        exDetails.put("error","Validation failed");

        return ResponseEntity.badRequest().body(exDetails);
    }
    @ExceptionHandler(FileProcessedException.class)
    ResponseEntity<Map<String,String>> handelFileAlreadyUploaded(FileProcessedException exception){
        Map<String,String> exDetails = new HashMap<>();
        exDetails.put("details", exception.getMessage());
        return ResponseEntity.badRequest().body(exDetails);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String,String>> handleIllegalArgument(IllegalArgumentException exception){
        Map<String,String> exDetails = new HashMap<>();
        exDetails.put("details", exception.getMessage());
        return ResponseEntity.badRequest().body(exDetails);
    }
}
