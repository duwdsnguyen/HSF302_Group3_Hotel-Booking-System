package hsf.g3.hotel_booking_system.enums.room;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public enum BookingAction {
     CREATE_BOOKING,
     UPDATE_BOOKING,
     CONFIRM_BOOKING,
     CHECK_IN,
     CHECK_OUT,
     CANCEL_BOOKING,
     CHANGE_ROOM;
}
