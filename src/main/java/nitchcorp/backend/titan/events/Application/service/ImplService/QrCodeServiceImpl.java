package nitchcorp.backend.titan.events.Application.service.ImplService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.service.QrCodeService;
import nitchcorp.backend.titan.events.domain.model.PurchasedTicket;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import nitchcorp.backend.titan.shared.minio.enums.FileType;
import nitchcorp.backend.titan.shared.minio.dto.FileUploadResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class QrCodeServiceImpl implements QrCodeService {
    private static final String VOUCHER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int QR_CODE_SIZE = 300;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMdd");

    private static final SecureRandom random = new SecureRandom();

    private final MinioService minioService;

    public QrCodeServiceImpl(MinioService minioService) {
        this.minioService = minioService;
        log.info("QrCodeService initialisé avec MinIO configuré");
    }

    @Override
    public String generateAndUploadQrCode(PurchasedTicket purchasedTicket) {
        validatePurchasedTicket(purchasedTicket);

        try {
            log.info("Génération QR code pour ticket: {}", purchasedTicket.getTicketTrackingId());

            String voucherCode = generateVoucherCode(purchasedTicket);
            String qrData = buildQrCodeData(purchasedTicket, voucherCode);
            byte[] qrCodeBytes = generateQrCodeBytes(qrData);

            String minioUrl = uploadToMinio(purchasedTicket, qrCodeBytes);

            log.info("QR code uploadé sur MinIO: {}", minioUrl);
            return minioUrl;

        } catch (Exception e) {
            log.error("Erreur génération QR code ticket {}: {}",
                    purchasedTicket.getTicketTrackingId(), e.getMessage());
            throw new QrCodeGenerationException("Impossible de générer le QR code", e);
        }
    }

    @Override
    public String generateVoucherCode(PurchasedTicket purchasedTicket) {
        validatePurchasedTicket(purchasedTicket);

        try {
            String eventPrefix = extractEventPrefix(purchasedTicket);
            String typePrefix = extractTypePrefix(purchasedTicket);
            String randomPart = generateRandomString(6);
            String datePart = LocalDateTime.now().format(DATE_FORMATTER);

            String voucherCode = String.format("%s-%s-%s-%s", eventPrefix, typePrefix, randomPart, datePart);

            log.debug("Voucher code généré: {}", voucherCode);
            return voucherCode;

        } catch (Exception e) {
            log.error("Erreur génération voucher code ticket {}: {}",
                    purchasedTicket.getTicketTrackingId(), e.getMessage());
            throw new QrCodeGenerationException("Impossible de générer le code voucher", e);
        }
    }

    @Override
    public void deleteQrCode(String qrCodeUrl) {
        if (qrCodeUrl == null || qrCodeUrl.isEmpty()) return;

        try {
            String fileName = minioService.extractFileNameFromUrl(qrCodeUrl);
            if (fileName != null) {
                boolean deleted = minioService.deleteFile(fileName, FileType.IMAGE);
                if (deleted) {
                    log.info("QR code supprimé de MinIO: {}", fileName);
                } else {
                    log.warn("Impossible de supprimer le QR code: {}", fileName);
                }
            }
        } catch (Exception e) {
            log.error("Erreur suppression QR code: {}", qrCodeUrl, e);
        }
    }

    @Override
    public TicketCodesResult generateTicketCodes(PurchasedTicket purchasedTicket) {
        validatePurchasedTicket(purchasedTicket);

        try {
            log.info("Génération codes complets pour ticket: {}", purchasedTicket.getTicketTrackingId());

            String voucherCode = generateVoucherCode(purchasedTicket);
            String qrData = buildQrCodeData(purchasedTicket, voucherCode);
            byte[] qrCodeBytes = generateQrCodeBytes(qrData);
            String qrCodeUrl = uploadToMinio(purchasedTicket, qrCodeBytes);

            return new TicketCodesResult(qrCodeUrl, voucherCode);

        } catch (Exception e) {
            log.error("Erreur génération codes ticket {}: {}",
                    purchasedTicket.getTicketTrackingId(), e.getMessage());
            throw new QrCodeGenerationException("Impossible de générer les codes du ticket", e);
        }
    }

    @Override
    public String generateOptimizedQrCodeUrl(String originalUrl, int width, int height) {
        validateDimensions(width, height);
        
        // MinIO ne supporte pas la transformation d'images comme Cloudinary
        // On retourne l'URL originale car les QR codes sont déjà optimisés à la génération
        log.info("MinIO ne supporte pas la transformation d'images. URL originale retournée: {}", originalUrl);
        return originalUrl;
    }

    @Override
    public QrCodeVariants getQrCodeVariants(String originalUrl) {
        // MinIO ne supporte pas les variants d'images comme Cloudinary
        // On retourne la même URL pour tous les variants
        log.info("MinIO ne supporte pas les variants d'images. Même URL utilisée pour tous les variants.");
        return new QrCodeVariants(
                originalUrl,  // Thumbnail
                originalUrl,  // Small
                originalUrl,  // Medium
                originalUrl,  // Large
                originalUrl   // Original
        );
    }

    @Override
    public String uploadToMinio(PurchasedTicket purchasedTicket, byte[] qrCodeBytes) throws Exception {
        String fileName = generateMinioFileName(purchasedTicket);
        
        // Créer un MultipartFile à partir des bytes
        MultipartFile multipartFile = new MockMultipartFile(
                "qrcode",
                fileName,
                "image/png",
                new ByteArrayInputStream(qrCodeBytes)
        );
        
        // Upload vers MinIO
        FileUploadResponse uploadResponse = minioService.uploadFile(multipartFile, FileType.IMAGE, fileName);
        
        log.info("QR code uploadé vers MinIO: {}", uploadResponse.getFileUrl());
        return uploadResponse.getFileUrl();
    }

    @Override
    public byte[] generateQrCodeBytes(String qrData) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        }
    }

    @Override
    public String generateMinioFileName(PurchasedTicket purchasedTicket) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        return String.format("qrcode_ticket_%s_%s.png", purchasedTicket.getTicketTrackingId(), timestamp);
    }

    @Override
    public String buildQrCodeData(PurchasedTicket purchasedTicket, String voucherCode) {
        return String.format(
                "TICKET:%s|BUYER:%s|EVENT:%s|TYPE:%s|PRICE:%.2f|QTY:%d|STATUS:%s|DATE:%s|VOUCHER:%s",
                purchasedTicket.getTicketTrackingId(),
                purchasedTicket.getBuyer().getFirstName() + " " + purchasedTicket.getBuyer().getLastName(),
                purchasedTicket.getTicketTemplate().getEvent().getTrackingId(),
                purchasedTicket.getTicketTemplate().getType(),
                purchasedTicket.getTicketTemplate().getPrice(),
                purchasedTicket.getNumberOfTicketsBought(),
                purchasedTicket.getStatus(),
                purchasedTicket.getDateCreated(),
                voucherCode
        );
    }

    @Override
    public String extractEventPrefix(PurchasedTicket purchasedTicket) {
        return purchasedTicket.getTicketTemplate().getEvent().getTrackingId()
                .toString().substring(0, 4).toUpperCase();
    }

    @Override
    public String extractTypePrefix(PurchasedTicket purchasedTicket) {
        String typeName = purchasedTicket.getTicketTemplate().getType().name();
        return typeName.length() >= 3 ? typeName.substring(0, 3) : typeName;
    }

    @Override
    public String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(VOUCHER_CHARS.charAt(random.nextInt(VOUCHER_CHARS.length())));
        }
        return sb.toString();
    }

    @Override
    public String extractFileNameFromUrl(String minioUrl) {
        return minioService.extractFileNameFromUrl(minioUrl);
    }

    @Override
    public void validatePurchasedTicket(PurchasedTicket purchasedTicket) {
        if (purchasedTicket == null) {
            throw new IllegalArgumentException("PurchasedTicket ne peut pas être null");
        }
        if (purchasedTicket.getTicketTrackingId() == null) {
            throw new IllegalArgumentException("TicketTrackingId ne peut pas être null");
        }
        if (purchasedTicket.getTicketTemplate() == null) {
            throw new IllegalArgumentException("TicketTemplate ne peut pas être null");
        }
        if (purchasedTicket.getTicketTemplate().getEvent() == null) {
            throw new IllegalArgumentException("Event ne peut pas être null");
        }
    }

    @Override
    public void validateDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Les dimensions doivent être positives");
        }
        if (width > 1000 || height > 1000) {
            throw new IllegalArgumentException("Les dimensions ne peuvent pas dépasser 1000px pour les QR codes");
        }
    }

    public static class TicketCodesResult {
        private final String qrCodeUrl;
        private final String voucherCode;

        public TicketCodesResult(String qrCodeUrl, String voucherCode) {
            this.qrCodeUrl = qrCodeUrl;
            this.voucherCode = voucherCode;
        }

        public String getQrCodeUrl() { return qrCodeUrl; }
        public String getVoucherCode() { return voucherCode; }

        @Override
        public String toString() {
            return String.format("TicketCodesResult{qrCodeUrl='%s', voucherCode='%s'}", qrCodeUrl, voucherCode);
        }
    }

    public static class QrCodeVariants {
        private final String thumbnail;
        private final String small;
        private final String medium;
        private final String large;
        private final String original;

        public QrCodeVariants(String thumbnail, String small, String medium, String large, String original) {
            this.thumbnail = thumbnail;
            this.small = small;
            this.medium = medium;
            this.large = large;
            this.original = original;
        }

        public String getThumbnail() { return thumbnail; }
        public String getSmall() { return small; }
        public String getMedium() { return medium; }
        public String getLarge() { return large; }
        public String getOriginal() { return original; }

        @Override
        public String toString() {
            return String.format("QrCodeVariants{thumbnail='%s', small='%s', medium='%s', large='%s', original='%s'}",
                    thumbnail, small, medium, large, original);
        }
    }

    public static class QrCodeGenerationException extends RuntimeException {
        public QrCodeGenerationException(String message, Throwable cause) {
            super(message, cause);
        }

        public QrCodeGenerationException(String message) {
            super(message);
        }
    }
}
