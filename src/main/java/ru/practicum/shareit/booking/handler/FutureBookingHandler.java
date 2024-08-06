package ru.practicum.shareit.booking.handler;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public class FutureBookingHandler extends BookingHandler {
    public FutureBookingHandler(BookingRepository bookingRepository) {
        super(bookingRepository);
    }

    @Override
    public List<Booking> handleRequest(List<Item> items, Long bookerId, BookingState bookingState, Sort sort) {
        if (bookingState == BookingState.FUTURE) {
            if (bookerId == null) {
                return bookingRepository.findAllByItemInAndEndAfter(items, LocalDateTime.now(), sort);
            } else {
                return bookingRepository.findByBookerIdAndEndAfter(bookerId, LocalDateTime.now(), sort);
            }

        } else if (nextHandler != null) {
            return nextHandler.handleRequest(items, bookerId, bookingState, sort);
        } else {
            throw new UnsupportedOperationException("Невозможно выполнить операцию");
        }
    }
}
