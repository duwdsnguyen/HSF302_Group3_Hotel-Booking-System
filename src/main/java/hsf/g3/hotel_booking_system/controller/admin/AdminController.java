package hsf.g3.hotel_booking_system.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/admin/dashboard")
public class AdminController {

    @GetMapping
    public String displayDashboard(){
        return "pages/admin/dashboard";
    }
}
