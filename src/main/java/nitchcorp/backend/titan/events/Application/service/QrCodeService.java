package nitchcorp.backend.titan.events.Application.service;

import nitchcorp.backend.titan.events.Application.service.ImplService.QrCodeServiceImpl;
import nitchcorp.backend.titan.events.domain.model.PurchasedTicket;

public interface QrCodeService {
    String generateAndUploadQrCode(PurchasedTicket purchasedTicket);

    String generateVoucherCode(PurchasedTicket purchasedTicket);

    void deleteQrCode(String qrCodeUrl);

    QrCodeServiceImpl.TicketCodesResult generateTicketCodes(PurchasedTicket purchasedTicket);

    String generateOptimizedQrCodeUrl(String originalUrl, int width, int height);

    QrCodeServiceImpl.QrCodeVariants getQrCodeVariants(String originalUrl);

    String uploadToMinio(PurchasedTicket purchasedTicket, byte[] qrCodeBytes) throws Exception;

    byte[] generateQrCodeBytes(String qrData) throws Exception;

    String generateMinioFileName(PurchasedTicket purchasedTicket);

    String buildQrCodeData(PurchasedTicket purchasedTicket, String voucherCode);

    String extractEventPrefix(PurchasedTicket purchasedTicket);

    String extractTypePrefix(PurchasedTicket purchasedTicket);

    String generateRandomString(int length);

    String extractFileNameFromUrl(String minioUrl);

    void validatePurchasedTicket(PurchasedTicket purchasedTicket);

    void validateDimensions(int width, int height);
}
