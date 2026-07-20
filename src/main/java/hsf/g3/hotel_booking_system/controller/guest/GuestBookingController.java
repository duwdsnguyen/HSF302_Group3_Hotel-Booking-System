package hsf.g3.hotel_booking_system.controller.guest;

import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import hsf.g3.hotel_booking_system.repository.admin.HotelServiceRepository;
import hsf.g3.hotel_booking_system.service.guest.GuestBookingService;
import hsf.g3.hotel_booking_system.service.guest.GuestRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/v1/guest/booking")
@RequiredArgsConstructor
public class GuestBookingController {

    private final GuestBookingService guestBookingService;
    private final GuestRoomService guestRoomService;
    private final HotelServiceRepository hotelServiceRepository;

    @GetMapping("/create")
    public String showBookingForm(
            @RequestParam Integer roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam Integer numberOfGuests,
            HttpSession session,
            Model model) {

        UserInfoDTO loggedInUser = (UserInfoDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/v1/auth/login";
        }

        try {
            Room room = guestRoomService.getRoomById(roomId);
            long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            BigDecimal totalRoomsAmount = room.getRoomType().getBasePrice().multiply(BigDecimal.valueOf(days));

            List<HotelService> availableServices = hotelServiceRepository.findByStatus(ServiceStatus.ACTIVE);

            model.addAttribute("room", room);
            model.addAttribute("roomId", roomId);
            model.addAttribute("availableServices", availableServices);
            model.addAttribute("checkInDate", checkInDate);
            model.addAttribute("checkOutDate", checkOutDate);
            model.addAttribute("numberOfGuests", numberOfGuests);
            model.addAttribute("days", days);
            model.addAttribute("totalRoomsAmount", totalRoomsAmount);
            model.addAttribute("user", loggedInUser);

            return "pages/guest/book_room";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/v1/guest/search-rooms";
        }
    }

    @PostMapping("/create")
    public String submitBooking(
            @org.springframework.web.bind.annotation.ModelAttribute hsf.g3.hotel_booking_system.dto.guest.request.BookingRequestDTO request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserInfoDTO loggedInUser = (UserInfoDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/v1/auth/login";
        }

        try {
            Long customerId = loggedInUser.getUserId();
            guestBookingService.createBooking(request, customerId);
            return "redirect:/v1/guest/booking/success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            
            return "redirect:/v1/guest/booking/create?roomId=" + request.getRoomId() +
                    "&checkInDate=" + request.getCheckInDate() +
                    "&checkOutDate=" + request.getCheckOutDate() +
                    "&numberOfGuests=" + request.getNumberOfGuests();
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "pages/guest/booking_success";
    }
}
