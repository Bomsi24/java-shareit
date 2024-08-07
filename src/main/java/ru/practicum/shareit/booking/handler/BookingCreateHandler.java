package ru.practicum.shareit.booking.handler;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BookingCreateHandler {
    private final BookingHandler firstHandler;

    public BookingCreateHandler(BookingRepository bookingRepository) {
        List<BookingHandler> handlers = new ArrayList<>();
        handlers.add(new AllBookingHandler(bookingRepository));
        handlers.add(new PastBookingHandler(bookingRepository));
        handlers.add(new CurrentBookingHandler(bookingRepository));
        handlers.add(new FutureBookingHandler(bookingRepository));
        handlers.add(new WaitingBookingHandler(bookingRepository));
        handlers.add(new RejectedBookingHandler(bookingRepository));

        IntStream.range(0, handlers.size() - 1)
                .forEach(i -> handlers.get(i).setNextHandler(handlers.get(i + 1)));

        this.firstHandler = handlers.getFirst();
    }

    public List<Booking> retrieveBookings(List<Item> items, Long bookerId, BookingState bookingState, Sort sort) {
        return firstHandler.handleRequest(items, bookerId, bookingState, sort);
    }
}
