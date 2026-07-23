package hsf.g3.hotel_booking_system.repository.guest;

import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId")
    Optional<Booking> findByIdForUpdate(Integer bookingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b " +
            "WHERE b.actualCheckIn IS NOT NULL " +
            "AND b.actualCheckOut IS NULL " +
            "AND b.customer.userId = :userId " +
            "AND b.room.roomId = :roomId")
    Optional<Booking> getCheckedInBooking( Long userId, Integer roomId);

    @Query("SELECT b.room FROM Booking b " +
            "WHERE b.actualCheckIn IS NOT NULL " +
            "AND b.actualCheckOut IS NULL " +
            "AND b.status = hsf.g3.hotel_booking_system.enums.room.BookingStatus.CHECKED_IN " +
            "AND b.customer.userId = :userId " +
            "ORDER BY b.room.roomNumber")
    List<Room> getCheckedInRooms(Long userId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.room.roomId = :roomId " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate " +
            "AND b.status IN :blockingStatuses")
    boolean existsBlockingBooking(Integer roomId, LocalDate checkInDate, LocalDate checkOutDate, Collection<BookingStatus> blockingStatuses);


    @Query("SELECT b FROM Booking b WHERE " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:fullName IS NULL OR LOWER(b.customer.fullName) LIKE LOWER(:fullName)) AND " +
            "(:minPrice IS NULL OR b.totalAmount >= :minPrice) AND " +
            "(:maxPrice IS NULL OR b.totalAmount <= :maxPrice) " +
            "ORDER BY b.createdAt DESC")
    List<Booking> searchBooking(
            @Param("status") BookingStatus status,
            @Param("fullName") String fullName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.customer c " +
            "LEFT JOIN FETCH b.room r " +
            "WHERE (:bookingId IS NULL OR b.id = :bookingId) " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (:customerName IS NULL OR LOWER(c.fullName) LIKE LOWER(:customerName)) " +
            "AND (:phone IS NULL OR c.phone LIKE :phone) " +
            "AND (:checkInFrom IS NULL OR b.checkInDate >= :checkInFrom) " +
            "AND (:checkInTo IS NULL OR b.checkInDate <= :checkInTo) " +
            "AND (:checkOutFrom IS NULL OR b.checkOutDate >= :checkOutFrom) " +
            "AND (:checkOutTo IS NULL OR b.checkOutDate <= :checkOutTo) " +
            "AND (:minPrice IS NULL OR b.totalAmount >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.totalAmount <= :maxPrice)")
    Page<Booking> searchBookingPaged(
            @Param("bookingId") Integer bookingId,
            @Param("status") BookingStatus status,
            @Param("customerName") String customerName,
            @Param("phone") String phone,
            @Param("checkInFrom") LocalDate checkInFrom,
            @Param("checkInTo") LocalDate checkInTo,
            @Param("checkOutFrom") LocalDate checkOutFrom,
            @Param("checkOutTo") LocalDate checkOutTo,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    List<Object[]> countByStatus();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b " +
            "WHERE b.status IN (:revenueStatuses)")
    BigDecimal sumTotalAmountByStatus(@Param("revenueStatuses") List<BookingStatus> revenueStatuses);

    // ── Guest Booking History ──────────────────────────────────────────────────

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.room r " +
            "JOIN FETCH r.roomType " +
            "WHERE b.customer.userId = :customerId " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findAllByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.room r " +
            "JOIN FETCH r.roomType " +
            "WHERE b.customer.userId = :customerId " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (:keyword IS NULL OR " +
            "     LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(r.roomType.typeName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY b.createdAt DESC")
    List<Booking> searchByCustomer(
            @Param("customerId") Long customerId,
            @Param("status") BookingStatus status,
            @Param("keyword") String keyword
    );

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.room r " +
            "JOIN FETCH r.roomType " +
            "LEFT JOIN FETCH b.bookingServices bs " +
            "LEFT JOIN FETCH bs.service " +
            "WHERE b.id = :bookingId " +
            "AND b.customer.userId = :customerId")
    Optional<Booking> findByIdAndCustomerId(
            @Param("bookingId") Integer bookingId,
            @Param("customerId") Long customerId
    );
}