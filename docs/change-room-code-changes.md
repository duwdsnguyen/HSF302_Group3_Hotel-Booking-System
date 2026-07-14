# Tổng quan thay đổi code chức năng Change Room

## 1. Luồng hoạt động mới

Luồng đổi phòng được tách thành ba bước:

1. Người dùng truy cập `/v1/guest/room/change` để xem danh sách phòng đang `AVAILABLE`.
2. Người dùng chọn một phòng để mở `/v1/guest/room/change/{roomId}` và xem chi tiết.
3. Tại trang chi tiết, người dùng chọn phòng hiện đang check-in, xác nhận đổi phòng và xem lịch sử đổi phòng của tài khoản.

> Hiện tại hệ thống thực hiện đổi phòng ngay lập tức, không tạo yêu cầu chờ lễ tân phê duyệt.

## 2. Controller

### `GuestController.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/controller/guest/GuestController.java
```

Các thay đổi chính:

- `GET /v1/guest/room/change`
  - Chỉ tải danh sách phòng đang available.
  - Không còn tải phòng đang check-in hoặc đặt form đổi phòng trên từng card.
- `GET /v1/guest/room/change/{roomId}`
  - Hiển thị chi tiết phòng được chọn.
  - Tải danh sách phòng đang check-in của người dùng.
  - Tải lịch sử đổi phòng của người dùng.
  - Tạo biến `canChange` để quyết định có hiển thị form đổi phòng hay không.
- `POST /v1/guest/room/change`
  - Gọi service để thực hiện đổi phòng.
  - Gửi flash message thành công hoặc thất bại.
  - Redirect về trang chi tiết phòng để người dùng thấy kết quả và lịch sử mới nhất.

## 3. Service

### `GuestRoomService.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/service/guest/GuestRoomService.java
```

Bổ sung method lấy lịch sử đổi phòng:

```java
List<RoomChangeHistoryDTO> getRoomChangeHistory(UserInfoDTO userInfoDTO);
```

### `GuestRoomServiceImpl.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/service/guest/GuestRoomServiceImpl.java
```

Các thay đổi chính:

- `getAllAvailableRoom()`
  - Lấy danh sách phòng có trạng thái `AVAILABLE`.
  - Chuyển `Room` sang `RoomDTO` trước khi trả về giao diện.
- `getCheckedInRooms()`
  - Chỉ lấy những phòng có booking đã check-in và chưa checkout.
  - Không còn lấy tất cả booking của người dùng.
- `getRoomChangeHistory()`
  - Lấy lịch sử có action `CHANGE_ROOM` của tài khoản đang đăng nhập.
  - Chuyển entity lịch sử sang `RoomChangeHistoryDTO`.
- `changeRoom()`
  - Kiểm tra người dùng đã đăng nhập.
  - Kiểm tra phòng hiện tại và phòng mới đã được chọn.
  - Không cho phép đổi sang chính phòng hiện tại.
  - Kiểm tra booking đang check-in thuộc đúng người dùng.
  - Khóa phòng mới trước khi cập nhật.
  - Kiểm tra phòng mới còn `AVAILABLE`.
  - Kiểm tra sức chứa của phòng mới.
  - Kiểm tra lịch đặt phòng bị chồng lấn trong thời gian lưu trú còn lại.
  - Chuyển phòng cũ thành `AVAILABLE`.
  - Chuyển phòng mới thành `OCCUPIED`.
  - Cập nhật booking sang phòng mới nhưng giữ nguyên trạng thái booking.
  - Ghi lại lịch sử phòng cũ, phòng mới, người thực hiện và thời gian.

Toàn bộ quá trình đổi phòng được đặt trong `@Transactional` để tránh dữ liệu chỉ được cập nhật một phần.

## 4. Repository

### `BookingRepository.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/repository/guest/BookingRepository.java
```

Các thay đổi chính:

- Sửa điều kiện lấy booking đang check-in:

```java
b.actualCheckIn IS NOT NULL
AND b.actualCheckOut IS NULL
```

- Thêm pessimistic lock khi lấy booking để đổi phòng.
- Thêm query lấy danh sách phòng đang check-in của người dùng.
- Thêm query `existsBlockingBooking()` để kiểm tra phòng mới có booking chồng lấn hay không.

### `RoomRepository.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/repository/admin/RoomRepository.java
```

Thêm method khóa phòng mới trước khi cập nhật:

```java
Optional<Room> findRoomByRoomIdForUpdate(Integer roomId);
```

Pessimistic lock giúp hạn chế trường hợp hai người dùng cùng đổi vào một phòng tại cùng thời điểm.

### `BookingHistoryRepository.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/repository/guest/BookingHistoryRepository.java
```

Thêm query lấy lịch sử đổi phòng với các điều kiện:

- Booking thuộc người dùng đang đăng nhập.
- Người thực hiện thay đổi là chính người dùng đó.
- Action là `CHANGE_ROOM`.
- Kết quả được sắp xếp từ mới nhất đến cũ nhất.

## 5. Entity và enum

### `BookingHistory.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/entity/guest/BookingHistory.java
```

Bổ sung các trường:

```java
Room oldRoom;
Room newRoom;
```

Nhờ đó lịch sử có thể hiển thị chính xác phòng cũ và phòng mới, kể cả sau khi `booking.room` đã được cập nhật.

Trường `action` được cấu hình lưu dưới dạng chuỗi:

```java
@Enumerated(EnumType.STRING)
BookingAction action;
```

### `BookingAction.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/enums/room/BookingAction.java
```

Bổ sung action:

```java
CHANGE_ROOM
```

### `Booking.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/entity/guest/Booking.java
```

Bổ sung quan hệ một-nhiều giữa booking và lịch sử:

```java
List<BookingHistory> bookingHistories;
```

## 6. DTO

### `RoomChangeRequest.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/dto/guest/room/request/RoomChangeRequest.java
```

Bổ sung `oldRoomId` để xác định phòng đang check-in mà người dùng muốn thay thế:

```java
Integer oldRoomId;
```

### `RoomChangeHistoryDTO.java`

Đường dẫn:

```text
src/main/java/hsf/g3/hotel_booking_system/dto/guest/room/response/RoomChangeHistoryDTO.java
```

DTO mới chứa:

- ID lịch sử.
- ID booking.
- Số phòng cũ.
- Số phòng mới.
- Thời gian thay đổi.
- Nội dung mô tả.

DTO cũng định dạng thời gian theo mẫu `dd/MM/yyyy HH:mm` để hiển thị trên giao diện.

## 7. Giao diện

### `room_change.html`

Đường dẫn:

```text
src/main/resources/templates/pages/guest/room/room_change.html
```

Các thay đổi chính:

- Trang chỉ hiển thị danh sách phòng available.
- Bỏ form đổi phòng khỏi từng room card.
- Mỗi card có nút `View room details` dẫn tới trang chi tiết.
- Giữ lại chức năng sắp xếp và phân trang.

### `room_change_detail.html`

Đường dẫn:

```text
src/main/resources/templates/pages/guest/room/room_change_detail.html
```

Đây là trang mới, bao gồm:

- Thông tin loại phòng, tầng, sức chứa, giá, trạng thái và mô tả.
- Form chọn phòng hiện đang check-in.
- Nút xác nhận đổi phòng.
- Thông báo thành công hoặc thất bại.
- Bảng lịch sử đổi phòng của người dùng.

### `room-change.css`

Đường dẫn:

```text
src/main/resources/static/css/room/room-change.css
```

CSS được cập nhật cho:

- Danh sách room card.
- Trang chi tiết hai cột trên desktop.
- Form đổi phòng.
- Bảng lịch sử.
- Trạng thái available, occupied và maintenance.
- Responsive trên màn hình điện thoại.
- Focus state hỗ trợ thao tác bằng bàn phím.

## 8. Unit test

### `GuestRoomServiceImplTest.java`

Đường dẫn:

```text
src/test/java/hsf/g3/hotel_booking_system/service/GuestRoomServiceImplTest.java
```

Các trường hợp đã được kiểm tra:

1. Đổi phòng thành công và ghi đúng lịch sử.
2. Từ chối khi phòng mới không còn available.
3. Từ chối khi phòng mới trùng phòng hiện tại.
4. Trả về thất bại khi không có thông tin đăng nhập.

Bốn unit test này đã chạy thành công. Việc chạy toàn bộ test của dự án chưa hoàn tất vì Maven cần tải thêm dependency và quá trình đã bị gián đoạn.
