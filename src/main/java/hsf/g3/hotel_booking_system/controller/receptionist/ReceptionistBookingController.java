package hsf.g3.hotel_booking_system.controller.receptionist;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.service.receptionist.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/v1/receptionist/bookings")
@RequiredArgsConstructor
public class ReceptionistBookingController {
    private final BookingService bookingService;

    @GetMapping
    public String listBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {
        List<Booking> bookings = bookingService.searchBookings(status, customerName, minPrice, maxPrice);

        model.addAttribute("bookings", bookings);
        model.addAttribute("bookingStatus", BookingStatus.values());
        model.addAttribute("currentStatus", status);
        model.addAttribute("customerName", customerName);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "pages/receptionist/booking_list";
    }

    @GetMapping("/{id}/detail")
    public String bookingDetail(@PathVariable("id") int bookingId, Model model) {
        try {
            BookingDetailDTO booking = bookingService.getBookingDetailsById(bookingId);
            model.addAttribute("booking", booking);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "pages/receptionist/booking_detail";
    }

    @GetMapping("/{id}/confirm")
    public String confirmBooking(@PathVariable("id") int bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.confirmBooking(bookingId);
            redirectAttributes.addFlashAttribute("message", "Đã xác nhận đơn booking #" + bookingId + " thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/v1/receptionist/bookings/" + bookingId + "/detail";
    }

    @GetMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable("id") int bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("message", "Đã hủy đơn booking #" + bookingId + " thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/v1/receptionist/bookings/" + bookingId + "/detail";
    }

    @GetMapping("/{id}/checkin")
    public String checkinBooking(@PathVariable("id") int bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.checkIn(bookingId);
            redirectAttributes.addFlashAttribute("message", "Đã check-in đơn booking " + bookingId + " thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/v1/receptionist/bookings/" + bookingId + "/detail";
    }

    @GetMapping("/{id}/checkout")
    public String checkoutBooking(@PathVariable("id") int bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.checkOut(bookingId);
            redirectAttributes.addFlashAttribute("message", "Đã check-out đơn booking " + bookingId + " thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/v1/receptionist/bookings/" + bookingId + "/detail";
    }
}
