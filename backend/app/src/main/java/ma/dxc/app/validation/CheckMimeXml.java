package ma.dxc.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ma.dxc.app.service.fileService.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class CheckMimeXml implements ConstraintValidator<CheckXml, MultipartFile> {

    private final FileStorageUtil fileStorageUtil;

    public CheckMimeXml(@Qualifier("tempFileStorageServiceimpl") FileStorageUtil fileStorageUtil) {
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public void initialize(CheckXml constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        try {
            if("xml".equals(fileStorageUtil.detectMimeType(value))){
            return true; }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
