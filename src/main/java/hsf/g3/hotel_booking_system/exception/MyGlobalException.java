package hsf.g3.hotel_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = "hsf.g3.hotel_booking_system.controller")
public class MyGlobalException {

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

    @ExceptionHandler(DuplicateServiceNameException.class)
    public String handleDuplicateServiceNameException (DuplicateServiceNameException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "pages/error/error_page";
    }

}
