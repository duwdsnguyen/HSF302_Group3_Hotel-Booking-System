package hsf.g3.hotel_booking_system.controller.user;

import hsf.g3.hotel_booking_system.dto.auth.LoginRequestDTO;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(){
        return "/pages/auth/login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute("loginRequestDTO")LoginRequestDTO loginRequestDTO){
        UserInfoDTO userInfoDTO = userService.login(loginRequestDTO);
        if(userInfoDTO == null){
            model.addAttribute("error", "Mật khẩu hoặc email đã sai");
            return "/pages/auth/login";
        }
        else{
            if(userInfoDTO.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(AppRole.ADMIN))){
                return "pages/admin/dashboard";
            }

            if(userInfoDTO.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(AppRole.GUEST))){
                return "pages/guest/dashboard";
            }
            else{
                return "pages/receptionist/dashboard";
            }
        }
    }
}
