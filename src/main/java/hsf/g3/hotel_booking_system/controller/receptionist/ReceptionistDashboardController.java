package hsf.g3.hotel_booking_system.controller.receptionist;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/receptionist/dashboard")
public class ReceptionistDashboardController {

    @GetMapping
    public String dashboard() {
        return "pages/receptionist/dashboard";
    }
}
