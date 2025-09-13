package nitchcorp.backend.titan.shared.minio.exception;

public class FileNotFoundException extends MinioException {
    public FileNotFoundException(String fileName) {
        super("File not found: " + fileName);
    }

    public FileNotFoundException(String fileName, Throwable cause) {
        super("File not found: " + fileName, cause);
    }
}
