package hsf.g3.hotel_booking_system.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SYSTEM_ERROR("COMMON","Lỗi hệ thống",HttpStatus.INTERNAL_SERVER_ERROR),

    RESET_TOKEN_NOT_FOUND("AUTHENTICATION","Không thể thay đổi mật khẩu",HttpStatus.NOT_FOUND),
    INVALID_CREDENTIAL("AUTHENTICATION","Email hoặc mật khẩu không chính xác",HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("AUTHENTICATION","Email đã được sử dụng",HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("AUTHENTICATION","Không tìm thấy người dùng",HttpStatus.NOT_FOUND),
    UNAUTHORIZED("AUTHENTICATION","Không có quyền", HttpStatus.BAD_REQUEST),
    
    ROOM_CHANGE_SELECTION_REQUIRED(
            "ROOM_CHANGE_002", "Vui lòng chọn phòng hiện tại và phòng muốn chuyển đến", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_SAME_ROOM(
            "ROOM_CHANGE_003", "Phòng mới phải khác phòng hiện tại", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_CHECKED_IN_BOOKING_NOT_FOUND(
            "ROOM_CHANGE_004", "Phòng hiện tại không thuộc danh sách đặt phòng đang check-in của bạn", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_BOOKING_NOT_CHECKED_IN(
            "ROOM_CHANGE_005", "Chỉ những phòng đang check-in mới được yêu cầu chuyển phòng", HttpStatus.CONFLICT),
    ROOM_CHANGE_REQUEST_ALREADY_PENDING(
            "ROOM_CHANGE_006", "Có yêu cầu chuyển phòng đang chờ duyệt", HttpStatus.CONFLICT),
    ROOM_CHANGE_ROOM_NOT_FOUND(
            "ROOM_CHANGE_007", "Không tìm thấy phòng trong yêu cầu chuyển phòng", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_ROOM_NOT_AVAILABLE(
            "ROOM_CHANGE_008", "Phòng muốn chuyển đến hiện không còn trống", HttpStatus.CONFLICT),
    ROOM_CHANGE_CAPACITY_EXCEEDED(
            "ROOM_CHANGE_009", "Phòng muốn chuyển đến không đủ sức chứa", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_BOOKING_NOT_ELIGIBLE(
            "ROOM_CHANGE_010", "Không còn đủ điều kiện chuyển phòng", HttpStatus.CONFLICT),
    ROOM_CHANGE_ROOM_RESERVED(
            "ROOM_CHANGE_011", "Phòng muốn chuyển đến đã được đặt trong thời gian lưu trú còn lại", HttpStatus.CONFLICT),
    ROOM_CHANGE_REQUEST_ID_REQUIRED(
            "ROOM_CHANGE_012", "Thiếu mã yêu cầu chuyển phòng", HttpStatus.BAD_REQUEST),
    ROOM_CHANGE_REQUEST_NOT_FOUND(
            "ROOM_CHANGE_013", "Không tìm thấy yêu cầu chuyển phòng", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_REQUEST_NOT_PENDING(
            "ROOM_CHANGE_014", "Chỉ yêu cầu đang chờ duyệt mới có thể được xử lý", HttpStatus.CONFLICT),
    ROOM_CHANGE_BOOKING_NOT_FOUND(
            "ROOM_CHANGE_017", "Không tìm thấy yêu cầu chuyển phòng", HttpStatus.NOT_FOUND),
    ROOM_CHANGE_ASSIGNMENT_CHANGED(
            "ROOM_CHANGE_018", "Booking không còn ở phòng cũ trong yêu cầu", HttpStatus.CONFLICT),
    ROOM_CHANGE_REJECTION_REASON_TOO_LONG(
            "ROOM_CHANGE_019", "Lý do từ chối không được vượt quá 500 ký tự", HttpStatus.BAD_REQUEST),

    ROOM_NOT_FOUND(
            "ROOM_001", "Không tìm thấy phòng", HttpStatus.NOT_FOUND),
    ROOM_NUMBER_DUPLICATE(
            "ROOM_002", "Số phòng đã tồn tại", HttpStatus.CONFLICT),

    ROOM_TYPE_NOT_FOUND(
            "ROOM_TYPE_001", "Không tìm thấy loại phòng", HttpStatus.NOT_FOUND),
    ROOM_TYPE_NAME_REQUIRED(
            "ROOM_TYPE_002", "Tên loại phòng không được để trống", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_MAX_GUESTS_INVALID(
            "ROOM_TYPE_003", "Số khách phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_BASE_PRICE_REQUIRED(
            "ROOM_TYPE_004", "Giá phòng không được để trống", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_BASE_PRICE_INVALID(
            "ROOM_TYPE_005", "Giá phòng phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_NAME_DUPLICATE(
            "ROOM_TYPE_006", "Loại phòng với tên này đã tồn tại", HttpStatus.CONFLICT),
    ROOM_TYPE_IMAGE_FILE_REQUIRED(
            "ROOM_TYPE_007", "Vui lòng chọn ít nhất một ảnh để tải lên", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_IMAGE_NOT_FOUND(
            "ROOM_TYPE_008", "Không tìm thấy ảnh", HttpStatus.NOT_FOUND),
    ROOM_TYPE_IMAGE_MISMATCH(
            "ROOM_TYPE_009", "Ảnh này không thuộc loại phòng đã chọn", HttpStatus.BAD_REQUEST),

    BOOKING_NOT_FOUND(
            "BOOKING_001", "Không tìm thấy đơn đặt phòng", HttpStatus.NOT_FOUND),
    BOOKING_CANCEL_NOT_ALLOWED(
            "BOOKING_002", "Chỉ có thể hủy đơn đặt phòng đang ở trạng thái chờ xác nhận hoặc đã xác nhận", HttpStatus.CONFLICT),


    SERVICE_NOT_FOUND("SERVICE_001","Không tìm thấy dịch vụ",HttpStatus.NOT_FOUND),

    SERVICE_NAME_REQUIRED("SERVICE_002","Tên dịch vụ không được để trống",HttpStatus.BAD_REQUEST),

    SERVICE_NAME_DUPLICATE("SERVICE_003","Tên dịch vụ đã tồn tại",HttpStatus.CONFLICT),

    SERVICE_PRICE_REQUIRED("SERVICE_004","Giá dịch vụ không được để trống",HttpStatus.BAD_REQUEST),

    SERVICE_PRICE_INVALID("SERVICE_005","Giá dịch vụ không được âm",HttpStatus.BAD_REQUEST),

    SERVICE_DATA_NULL("SERVICE_006","Dữ liệu dịch vụ không được null",HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
