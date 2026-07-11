package hsf.g3.hotel_booking_system.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;
    Integer fieldIntId;

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s : %s",resourceName,field,fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s : %d",resourceName,field,fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
    public ResourceNotFoundException(String resourceName, String field, Integer fieldIntId) {
        super(String.format("%s not found with %s : %d",resourceName,field,fieldIntId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldIntId = fieldIntId;
    }
}
