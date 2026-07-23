package hsf.g3.hotel_booking_system.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SYSTEM_ERROR("COMMON","Lỗi hệ thống",HttpStatus.INTERNAL_SERVER_ERROR),


    INVALID_CREDENTIAL("AUTHENTICATION","Email hoặc mật khẩu không chính xác",HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("AUTHENTICATION","Email đã được sử dụng",HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("AUTHENTICATION","Không tìm thấy người dùng",HttpStatus.NOT_FOUND),

    ROOM_CHANGE_AUTHENTICATION_REQUIRED(
            "ROOM_CHANGE_001", "Bạn cần đăng nhập để yêu cầu chuyển phòng", HttpStatus.UNAUTHORIZED),
    ROOM_CHANGE_SELECTION_REQUIRED(
            "ROOM_CHANGE_002", "Vui lòng chọn phòng hiện tại và phòng muốn chuyển đến", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_SAME_ROOM(
            "ROOM_CHANGE_003", "Phòng mới phải khác phòng hiện tại", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_CHECKED_IN_BOOKING_NOT_FOUND(
            "ROOM_CHANGE_004", "Phòng hiện tại không thuộc booking đang check-in của bạn", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_BOOKING_NOT_CHECKED_IN(
            "ROOM_CHANGE_005", "Chỉ booking đang check-in mới được yêu cầu chuyển phòng", HttpStatus.CONFLICT),
    ROOM_CHANGE_REQUEST_ALREADY_PENDING(
            "ROOM_CHANGE_006", "Booking này đã có yêu cầu chuyển phòng đang chờ duyệt", HttpStatus.CONFLICT),
    ROOM_CHANGE_ROOM_NOT_FOUND(
            "ROOM_CHANGE_007", "Không tìm thấy phòng trong yêu cầu chuyển phòng", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_ROOM_NOT_AVAILABLE(
            "ROOM_CHANGE_008", "Phòng muốn chuyển đến hiện không còn trống", HttpStatus.CONFLICT),
    ROOM_CHANGE_CAPACITY_EXCEEDED(
            "ROOM_CHANGE_009", "Phòng muốn chuyển đến không đủ sức chứa", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_BOOKING_NOT_ELIGIBLE(
            "ROOM_CHANGE_010", "Booking không còn đủ điều kiện chuyển phòng", HttpStatus.CONFLICT),
    ROOM_CHANGE_ROOM_RESERVED(
            "ROOM_CHANGE_011", "Phòng muốn chuyển đến đã được đặt trong thời gian lưu trú còn lại", HttpStatus.CONFLICT),
    ROOM_CHANGE_REQUEST_ID_REQUIRED(
            "ROOM_CHANGE_012", "Thiếu mã yêu cầu chuyển phòng", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_REQUEST_NOT_FOUND(
            "ROOM_CHANGE_013", "Không tìm thấy yêu cầu chuyển phòng", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_REQUEST_NOT_PENDING(
            "ROOM_CHANGE_014", "Chỉ yêu cầu đang chờ duyệt mới có thể được xử lý", HttpStatus.CONFLICT),
    ROOM_CHANGE_RECEPTIONIST_SESSION_INVALID(
            "ROOM_CHANGE_015", "Phiên đăng nhập của receptionist không hợp lệ", HttpStatus.UNAUTHORIZED),
    ROOM_CHANGE_RECEPTIONIST_NOT_FOUND(
            "ROOM_CHANGE_016", "Không tìm thấy tài khoản receptionist", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_BOOKING_NOT_FOUND(
            "ROOM_CHANGE_017", "Không tìm thấy booking của yêu cầu chuyển phòng", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_ASSIGNMENT_CHANGED(
            "ROOM_CHANGE_018", "Booking không còn ở phòng cũ trong yêu cầu", HttpStatus.CONFLICT),
    ROOM_CHANGE_REJECTION_REASON_TOO_LONG(
            "ROOM_CHANGE_019", "Lý do từ chối không được vượt quá 500 ký tự", HttpStatus.BAD_REQUEST);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
