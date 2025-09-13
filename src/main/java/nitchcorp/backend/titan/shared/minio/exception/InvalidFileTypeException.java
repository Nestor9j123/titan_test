package nitchcorp.backend.titan.shared.minio.exception;

public class InvalidFileTypeException extends MinioException {
    public InvalidFileTypeException(String mimeType) {
        super("Invalid file type: " + mimeType);
    }

    public InvalidFileTypeException(String mimeType, Throwable cause) {
        super("Invalid file type: " + mimeType, cause);
    }
}
