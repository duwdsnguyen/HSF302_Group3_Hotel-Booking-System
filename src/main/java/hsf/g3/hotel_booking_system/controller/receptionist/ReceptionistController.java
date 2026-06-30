package hsf.g3.hotel_booking_system.controller.receptionist;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository;
import hsf.g3.hotel_booking_system.service.receptionist.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/v1/receptionist")
@RequiredArgsConstructor
public class ReceptionistController {

    private final BookingService bookingService;
    private final RoomTypeRepository roomTypeRepository;

    @GetMapping("/check-in")
    public String checkInList(@RequestParam(required = false) String search,
                              @RequestParam(required = false) Double minPrice,
                              @RequestParam(required = false) Double maxPrice,
                              @RequestParam(required = false) Integer roomTypeId,
                              Model model) {
        List<BookingDetailDTO> dtoList = bookingService.getBookingsForCheckIn(search, minPrice, maxPrice, roomTypeId);

        model.addAttribute("bookings", dtoList);
        model.addAttribute("search", search);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("roomTypes", roomTypeRepository.findAll());
        return "pages/receptionist/check-in";
    }

    @PostMapping("/check-in")
    public String performCheckIn(@RequestParam int bookingId, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            bookingService.checkIn(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Check-in successful for booking ID " + bookingId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/v1/receptionist/check-in";
    }

    @GetMapping("/check-out")
    public String checkOutList(@RequestParam(required = false) String search,
                               @RequestParam(required = false) Double minPrice,
                               @RequestParam(required = false) Double maxPrice,
                               @RequestParam(required = false) Integer roomTypeId,
                               Model model) {
        List<BookingDetailDTO> dtoList = bookingService.getBookingForCheckOut(search, minPrice, maxPrice, roomTypeId);

        model.addAttribute("bookings", dtoList);
        model.addAttribute("search", search);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("roomTypes", roomTypeRepository.findAll());
        return "pages/receptionist/check-out";
    }

    @PostMapping("/check-out")
    public String performCheckOut(@RequestParam int bookingId, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            bookingService.checkOut(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Check-out successful for booking ID " + bookingId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/v1/receptionist/check-out";
    }
}
