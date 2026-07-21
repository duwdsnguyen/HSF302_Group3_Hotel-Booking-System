package hsf.g3.hotel_booking_system.controller.admin;

import hsf.g3.hotel_booking_system.service.admin.AdminRoomTypeImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/admin/room-types/{roomTypeId}/images")
public class RoomTypeImageController {

    private final AdminRoomTypeImageService imageService;

    /** Page: manage images for a room type */
    @GetMapping
    public String manageImages(@PathVariable Integer roomTypeId, Model model) {
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("images", imageService.getImages(roomTypeId));
        return "/pages/admin/room/room-type-images";
    }

    /** Upload one or more images */
    @PostMapping("/upload")
    public String uploadImages(
            @PathVariable Integer roomTypeId,
            @RequestParam("files") List<MultipartFile> files,
            RedirectAttributes redirectAttributes) {

        try {
            imageService.uploadImages(roomTypeId, files);
            redirectAttributes.addFlashAttribute("success", "Images uploaded successfully.");
        } catch (IllegalArgumentException | IOException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/v1/admin/room-types/" + roomTypeId + "/images";
    }

    /** Delete a single image */
    @PostMapping("/{imageId}/delete")
    public String deleteImage(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer imageId,
            RedirectAttributes redirectAttributes) {

        try {
            imageService.deleteImage(roomTypeId, imageId);
            redirectAttributes.addFlashAttribute("success", "Image deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/v1/admin/room-types/" + roomTypeId + "/images";
    }
}