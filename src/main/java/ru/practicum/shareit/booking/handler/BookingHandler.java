package ru.practicum.shareit.booking.handler;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RequiredArgsConstructor
public abstract class BookingHandler {
    protected final BookingRepository bookingRepository;
    @Setter
    protected BookingHandler nextHandler;

    abstract List<Booking> handleRequest(List<Item> items, Long bookerId, BookingState bookingState, Sort sort);
}
