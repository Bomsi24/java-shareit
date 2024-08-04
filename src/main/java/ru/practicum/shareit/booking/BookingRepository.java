package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByItemIn(Collection<Item> items);

    List<Booking> findByItemOwner_Id(int ownerId);

    @Query("SELECT DISTINCT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.id IN :items")
    List<Booking> findByBookingByItemsId(Set<Integer> items);

    Optional<Booking> findByBookerId(int bookerId);

    List<Booking> findAllByBookerId(int bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(int bookerId, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 " +
            " AND b.start < ?2 AND b.end > ?2 " +
            " AND b.status = 'APPROVED' ORDER BY ?3")
    List<Booking> findRelevantBookingsByBookingId(int bookerId, LocalDateTime currentTime, Sort sort);

    List<Booking> findByBookerIdAndEndAfter(int bookerId, LocalDateTime currentTime, Sort sort);

    List<Booking> findBookingByIdAndStatus(int bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);

    List<Booking> findAllByItemInAndEndIsBefore(List<Item> items, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item IN ?1 " +
            " AND b.start < ?2 AND b.end > ?2 " +
            " AND b.status = 'APPROVED' ORDER BY ?3")
    List<Booking> findRelevantBookingsByItem(List<Item> items, LocalDateTime currentTime, Sort sort);

    List<Booking> findAllByItemInAndEndAfter(List<Item> items, LocalDateTime currentTime, Sort sort);

    List<Booking> findAllByItemInAndStatus(List<Item> items, BookingStatus status, Sort sort);
}
