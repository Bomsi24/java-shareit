package ru.practicum.shareit.booking.handler;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public class PastBookingHandler extends BookingHandler {
    public PastBookingHandler(BookingRepository bookingRepository) {
        super(bookingRepository);
    }

    @Override
    public List<Booking> handleRequest(List<Item> items, Long bookerId, BookingState bookingState, Sort sort) {
        if (bookingState == BookingState.PAST) {
            if (bookerId == null) {
                return bookingRepository.findAllByItemInAndEndIsBefore(items, LocalDateTime.now(), sort);
            } else {
                return bookingRepository.findByBookerIdAndEndIsBefore(bookerId, LocalDateTime.now(), sort);
            }

        } else if (nextHandler != null) {
            return nextHandler.handleRequest(items, bookerId, bookingState, sort);
        } else {
            throw new UnsupportedOperationException("Невозможно выполнить операцию");
        }
    }
}
