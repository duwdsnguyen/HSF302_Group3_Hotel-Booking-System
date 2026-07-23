package hsf.g3.hotel_booking_system.service.admin;

import hsf.g3.hotel_booking_system.entity.room.RoomType;
import hsf.g3.hotel_booking_system.entity.room.RoomTypeImage;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeImageRepository;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoomTypeImageService {

    private final RoomTypeImageRepository imageRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ImageStorageService storageService;

    public List<RoomTypeImage> getImages(Integer roomTypeId) {
        return imageRepository.findByRoomType_RoomTypeIdOrderByDisplayOrderAsc(roomTypeId);
    }

    @Transactional
    public void uploadImages(Integer roomTypeId, List<MultipartFile> files) throws IOException {
        boolean hasFile = files != null && files.stream().anyMatch(file -> file != null && !file.isEmpty());
        if (!hasFile) {
            throw new IllegalArgumentException("Please select at least one image to upload.");
        }

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Room type not found."));

        int order = imageRepository
                .findByRoomType_RoomTypeIdOrderByDisplayOrderAsc(roomTypeId)
                .size();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            String url = storageService.store(file);

            RoomTypeImage image = RoomTypeImage.builder()
                    .roomType(roomType)
                    .imageUrl(url)
                    .displayOrder(order++)
                    .build();
            imageRepository.save(image);
        }
    }

    @Transactional
    public void deleteImage(Integer roomTypeId, Integer imageId) {
        RoomTypeImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found."));

        if (!image.getRoomType().getRoomTypeId().equals(roomTypeId)) {
            throw new IllegalArgumentException("Image does not belong to this room type.");
        }

        storageService.delete(image.getImageUrl());
        imageRepository.delete(image);
    }
}
