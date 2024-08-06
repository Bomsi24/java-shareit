package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto getBooking(Long userId, Long bookingId);

    BookingResponseDto create(Long userId, BookingDto booking);

    BookingResponseDto update(Long userId, Long bookingId, boolean approved);

    List<BookingResponseDto> getBookings(Long bookerId, String state);

    List<BookingResponseDto> getBookingsOwner(Long userId, String state);
}
