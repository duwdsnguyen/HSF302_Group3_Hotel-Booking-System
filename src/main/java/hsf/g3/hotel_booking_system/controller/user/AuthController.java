package hsf.g3.hotel_booking_system.controller.user;

import hsf.g3.hotel_booking_system.dto.auth.*;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "/pages/auth/login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute("loginRequestDTO")LoginRequestDTO loginRequestDTO,HttpSession session){
        UserInfoDTO userInfoDTO = userService.login(loginRequestDTO);
        LOGGER.info("User login with email: {} and password: {}",loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
        if(userInfoDTO == null){
            model.addAttribute("error", "Mật khẩu hoặc email đã sai");
            return "/pages/auth/login";
        }
        else{
            session.setAttribute("loggedInUser",userInfoDTO);
            if(userInfoDTO.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(AppRole.ADMIN))){
                LOGGER.info("Already at admin dashboard for role {}",userInfoDTO.getRoles().stream().filter((role -> role.getRoleCode().equals(AppRole.ADMIN))));
                return "redirect:/v1/admin/dashboard";
            }

            if(userInfoDTO.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(AppRole.GUEST))){
                LOGGER.info("Already at guest dashboard for role {}",userInfoDTO.getRoles().stream().filter((role -> role.getRoleCode().equals(AppRole.GUEST))));
                return "redirect:/v1/guest/dashboard";
            }
            else{
                LOGGER.info("Already at receptionist dashboard for role {}",userInfoDTO.getRoles().stream().filter((role -> role.getRoleCode().equals(AppRole.RECEPTIONIST))));
                return  "redirect:/v1/receptionist/dashboard";
            }
        }
    }

    @GetMapping("/logout")
    public String logout (HttpSession session){
        LOGGER.info("Logout successfully");
        session.invalidate();
        return "redirect:/v1/auth/login";
    }

    @GetMapping("/register")
    public String register(Model model){
        LOGGER.info("Display register page");
        model.addAttribute("registerRequestDTO",new RegisterRequestDTO());
        return "/pages/auth/register";
    }

    @PostMapping("/register")
    public String register(Model model, RedirectAttributes redirectAttributes, @ModelAttribute("registerRequestDTO")RegisterRequestDTO registerRequestDTO){
        UserInfoDTO userInfoDTO = userService.register(registerRequestDTO);
        if(userInfoDTO == null){
            model.addAttribute("error", "Không thể tạo tài khoản");
            model.addAttribute("registerRequestDTO",registerRequestDTO);
            return "/pages/auth/register";
        }
        else{
            redirectAttributes.addFlashAttribute("success","Tạo tài khoản thành công");
            return "redirect:/v1/auth/login";
        }
    }

    @GetMapping("/forget-password")
    public String forgetPassword(Model model){
        LOGGER.info("Display forget password page");
        model.addAttribute("forgetPasswordRequest",new ForgetPasswordRequest());
        return "/pages/auth/forget_password";
    }

    @PostMapping("/forget-password")
    public String forgetPassword(Model model,@ModelAttribute("forgetPasswordRequest") ForgetPasswordRequest forgetPasswordRequest){
        ForgetPasswordResponse forgetPasswordResponse = userService.forgetPassword(forgetPasswordRequest);
        if(forgetPasswordResponse != null){
            LOGGER.info("Link has been sent to your email: {}",forgetPasswordRequest.getEmail());
            model.addAttribute("success","Link url sẽ được gửi cho bạn ngay lập tức");
        }else{
            LOGGER.info("Link hasn't sent to your email: {}",forgetPasswordRequest.getEmail());
            model.addAttribute("error", "Nếu email bạn tồn tại thì sẽ nhận được mail trong giây lát");
        }
        return "/pages/auth/forget_password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model,RedirectAttributes redirectAttributes,@RequestParam(value = "token",required = false) String token){
        if(token == null || !userService.isValidToken(token)){
            LOGGER.info("Password reset failed for token {}: Invalid or expired token",token);
            redirectAttributes.addFlashAttribute("error","Token của bạn đã hết hạn hoặc không phù hợp");
            return "redirect:/v1/auth/forget-password";
        }else{
            model.addAttribute("resetPasswordRequest",new ResetPasswordRequest());
            model.addAttribute("token",token);
            return "/pages/auth/reset_password";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(RedirectAttributes redirectAttributes,@ModelAttribute("resetPasswordRequest")ResetPasswordRequest resetPasswordRequest,@RequestParam("token")String token){
        if(token == null || !userService.isValidToken(token)) {
            LOGGER.info("Password reset failed: Invalid or expired token");
            redirectAttributes.addFlashAttribute("error", "Token của bạn đã hết hạn hoặc không phù hợp");
            return "redirect:/v1/auth/forget-password";
        }
        if(!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmedPassword())){
            LOGGER.info("Password reset failed due to confirmed password not equal the new password");
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận ko trùng khớp");
            return "redirect:/v1/auth/reset-password?token=" + token;
        }
        boolean isSuccess = userService.resetPassword(resetPasswordRequest,token);
        LOGGER.debug("New password: {} updated by token: {}",resetPasswordRequest.getNewPassword(),token);
        if(isSuccess){
            LOGGER.info("Password reset succeeded");
            redirectAttributes.addFlashAttribute("success", "Bạn đã thay đổi thành công mật khẩu");
            return "redirect:/v1/auth/login";
        } else {
            LOGGER.info("Password reset failed due to internal server errors");
            redirectAttributes.addFlashAttribute("error", "Hệ thống bị lỗi. Vui lòng thử sau");
            return "redirect:/v1/auth/reset-password?token=" + token;
        }
    }
}
