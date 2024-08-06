package ru.practicum.shareit.booking.handler;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class WaitingBookingHandler extends BookingHandler {
    public WaitingBookingHandler(BookingRepository bookingRepository) {
        super(bookingRepository);
    }

    @Override
    public List<Booking> handleRequest(List<Item> items, Long bookerId, BookingState bookingState, Sort sort) {
        if (bookingState == BookingState.WAITING) {
            if (bookerId == null) {
                return bookingRepository.findAllByItemInAndStatus(items, BookingStatus.WAITING, sort);
            } else {
                return bookingRepository.findBookingByIdAndStatus(bookerId, BookingStatus.WAITING, sort);
            }

        } else if (nextHandler != null) {
            return nextHandler.handleRequest(items, bookerId, bookingState, sort);
        } else {
            throw new UnsupportedOperationException("Невозможно выполнить операцию");
        }
    }
}
