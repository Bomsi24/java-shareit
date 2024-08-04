package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto getBooking(int userId, int bookingId);

    BookingResponseDto create(int userId, BookingDto booking);

    BookingResponseDto update(int userId, int bookingId, boolean approved);

    List<BookingResponseDto> getBookings(int bookerId, String state);

    List<BookingResponseDto> getBookingsOwner(int userId, String state);
}
