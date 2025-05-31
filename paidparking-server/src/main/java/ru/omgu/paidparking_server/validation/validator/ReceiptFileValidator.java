package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import ru.omgu.paidparking_server.validation.annotation.ValidReceiptFile;

import java.util.Set;

public class ReceiptFileValidator implements ConstraintValidator<ValidReceiptFile, MultipartFile> {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png"
    );

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            return false;
        }

        return file.getSize() <= MAX_FILE_SIZE_BYTES;
    }
}