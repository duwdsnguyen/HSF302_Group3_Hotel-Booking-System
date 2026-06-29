package hsf.g3.hotel_booking_system.controller.guest;

import org.springframework.ui.Model;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.service.guest.GuestRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/v1/guest")
@RequiredArgsConstructor
public class GuestController {
    private final GuestRoomService guestRoomService;

    @GetMapping("/search-rooms")
    public String searchAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam Integer numberOfGuests,
            Model model) {
        try {
            List<Room> availableRooms = guestRoomService.searchAvailableRooms(checkInDate, checkOutDate, numberOfGuests);

            model.addAttribute("rooms", availableRooms);
            model.addAttribute("checkIn", checkInDate);
            model.addAttribute("checkOut", checkOutDate);
            model.addAttribute("guests", numberOfGuests);

            return "pages/guest/search_results";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/home";
        }
    }
}
