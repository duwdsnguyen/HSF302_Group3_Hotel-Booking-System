package hsf.g3.hotel_booking_system.controller.user;

import hsf.g3.hotel_booking_system.dto.auth.LoginRequestDTO;
import hsf.g3.hotel_booking_system.dto.auth.RegisterRequestDTO;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Level;

@Controller
@RequestMapping("/v1/auth")
public class AuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("loginRequestDTO",new LoginRequestDTO());
        return "pages/auth/login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute("loginRequestDTO")LoginRequestDTO loginRequestDTO){
        UserInfoDTO userInfoDTO = userService.login(loginRequestDTO);
        LOGGER.info("User login with email: {} and password: {}",loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
        if(userInfoDTO == null){
            model.addAttribute("error", "Mật khẩu hoặc email đã sai");
            return "pages/auth/login";
        }
        else{
            System.out.println("redirect to role dashboard");
            if(userInfoDTO.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(AppRole.ADMIN))){
//                System.out.println("redirect to admin dashboard");
                return "redirect:/admin/dashboard";
            }

            if(userInfoDTO.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(AppRole.GUEST))){
                return "pages/guest/dashboard";
            }
            else{
                return "pages/receptionist/dashboard";
            }
        }
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("registerRequestDTO",new RegisterRequestDTO());
        return "pages/auth/register";
    }

    @PostMapping("/register")
    public String register(Model model, RedirectAttributes redirectAttributes, @ModelAttribute("registerRequestDTO")RegisterRequestDTO registerRequestDTO){
        UserInfoDTO userInfoDTO = userService.register(registerRequestDTO);
        if(userInfoDTO == null){
            model.addAttribute("error", "Không thể tạo tài khoản");
            model.addAttribute("registerRequestDTO",registerRequestDTO);
            return "pages/auth/register";
        }
        else{
            redirectAttributes.addFlashAttribute("success","Tạo tài khoản thành công");
            return "redirect:/v1/auth/login";
        }
    }
}
