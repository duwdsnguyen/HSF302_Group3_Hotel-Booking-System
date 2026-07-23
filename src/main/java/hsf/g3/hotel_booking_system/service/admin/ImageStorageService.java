package hsf.g3.hotel_booking_system.service.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${app.upload.dir:src/main/resources/static/images/room-types}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB


    private Path resolveUploadDir() {
        Path p = Paths.get(uploadDir);
        if (p.isAbsolute()) return p;
        // user.dir = project root when running via IDE or `mvn spring-boot:run`
        return Paths.get(System.getProperty("user.dir")).resolve(p);
    }


    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only JPG, PNG, WEBP, GIF images are allowed.");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File size must not exceed 5 MB.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;

        Path dirPath = resolveUploadDir();
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // transferTo() requires absolute path — toAbsolutePath() ensures this
        Path filePath = dirPath.resolve(filename).toAbsolutePath();
        file.transferTo(filePath);

        return "/images/room-types/" + filename;
    }


    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String filename = Paths.get(imageUrl).getFileName().toString();
            Path filePath = resolveUploadDir().resolve(filename).toAbsolutePath();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("[ImageStorageService] Could not delete file: " + imageUrl + " — " + e.getMessage());
        }
    }
}