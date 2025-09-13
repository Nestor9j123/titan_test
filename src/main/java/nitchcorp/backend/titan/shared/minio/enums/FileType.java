package nitchcorp.backend.titan.shared.minio.enums;

import lombok.Getter;

@Getter
public enum FileType {
    SONG("songs", new String[]{"audio/mpeg", "audio/mp3", "audio/wav", "audio/flac", "audio/ogg"}),
    IMAGE("images", new String[]{"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp"}),
    VIDEO("videos", new String[]{"video/mp4", "video/avi", "video/mkv", "video/mov", "video/wmv", "video/webm"}),
    PHOTO("photos", new String[]{"image/jpeg", "image/jpg", "image/png", "image/raw", "image/tiff"});

    private final String bucketSuffix;
    private final String[] allowedMimeTypes;

    FileType(String bucketSuffix, String[] allowedMimeTypes) {
        this.bucketSuffix = bucketSuffix;
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public static FileType fromMimeType(String mimeType) {
        for (FileType type : values()) {
            for (String allowedType : type.allowedMimeTypes) {
                if (allowedType.equalsIgnoreCase(mimeType)) {
                    return type;
                }
            }
        }
        return null;
    }

    public boolean isValidMimeType(String mimeType) {
        for (String allowedType : allowedMimeTypes) {
            if (allowedType.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }
}
