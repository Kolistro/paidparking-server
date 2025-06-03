package ru.omgu.paidparking_server.dto.request;

import org.springframework.web.multipart.MultipartFile;
import ru.omgu.paidparking_server.validation.annotation.ValidReceiptFile;

public record PaymentRequestDto(
        @ValidReceiptFile
        MultipartFile receiptFile
) {
}
