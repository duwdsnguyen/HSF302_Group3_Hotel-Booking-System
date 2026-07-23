package hsf.g3.hotel_booking_system.controller.receptionist;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.service.receptionist.BookingService;
import hsf.g3.hotel_booking_system.dto.receptionist.BookingSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

@Controller
@RequestMapping("/v1/receptionist/bookings")
@RequiredArgsConstructor
public class ReceptionistBookingController {
    private final BookingService bookingService;

    @GetMapping
    public String listBookings(
            @RequestParam(required = false) Integer bookingId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutTo,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        BookingStatus bookingStatus = (status == null || status.isBlank() || status.equals("ALL")
                ? null : BookingStatus.valueOf(status));

        Page<Booking> bookingsPage = bookingService.searchBookingPaged(
                bookingId, bookingStatus, customerName, phone,
                checkInFrom, checkInTo, checkOutFrom, checkOutTo,
                minPrice, maxPrice, sortField, sortDir,
                page, size
        );

        BookingSummaryDTO summary = bookingService.getBookingSummary();


        model.addAttribute("bookings", bookingsPage.getContent());
        model.addAttribute("page", bookingsPage);
        model.addAttribute("summary", summary);
        model.addAttribute("bookingStatus", BookingStatus.values());
        model.addAttribute("currentStatus", (status == null || status.isBlank()) ? "ALL" : status);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("customerName", customerName);
        model.addAttribute("phone", phone);
        model.addAttribute("checkInFrom", checkInFrom);
        model.addAttribute("checkInTo", checkInTo);
        model.addAttribute("checkOutFrom", checkOutFrom);
        model.addAttribute("checkOutTo", checkOutTo);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", bookingsPage.getTotalPages());
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
