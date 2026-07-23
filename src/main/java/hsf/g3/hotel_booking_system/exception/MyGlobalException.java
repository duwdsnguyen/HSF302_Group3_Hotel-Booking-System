package hsf.g3.hotel_booking_system.exception;

import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = "hsf.g3.hotel_booking_system.controller")
public class MyGlobalException {

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException exception,
                                     HttpServletResponse response,
                                     Model model) {
        ErrorCode errorCode = exception.getErrorCode();
        response.setStatus(errorCode.getHttpStatus().value());
        model.addAttribute("errorCode", errorCode.getCode());
        model.addAttribute("error", "[" + errorCode.getCode() + "] " + errorCode.getMessage());
        return "pages/error/error_page";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ResourceNotFoundException (ResourceNotFoundException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "pages/error/404";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleWebException(Model model, Exception ex){
        ex.printStackTrace();
        String message = ex.getMessage() != null ? ex.getMessage() : "Lỗi hệ thống nội bộ.";
        model.addAttribute("error","Hệ thống có lỗi xảy ra: " + message);
        return "pages/error/error_page";
    }



}
