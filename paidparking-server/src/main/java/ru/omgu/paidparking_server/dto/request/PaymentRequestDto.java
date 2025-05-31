package ru.omgu.paidparking_server.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import ru.omgu.paidparking_server.validation.annotation.ValidReceiptFile;

public record PaymentRequestDto(
        @NotNull
        @ValidReceiptFile
        MultipartFile receiptFile
) {
}
