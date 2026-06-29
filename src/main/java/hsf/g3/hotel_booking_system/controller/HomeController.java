package hsf.g3.hotel_booking_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "pages/home";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/v1/auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "redirect:/v1/auth/register";
    }
}
