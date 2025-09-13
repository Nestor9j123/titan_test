package nitchcorp.backend.titan.shared.minio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;
    private String originalFileName;
    private String bucketName;
    private Long fileSize;
    private String contentType;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String etag;
}
