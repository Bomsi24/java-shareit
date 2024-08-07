package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.http.HttpHeadersConstants;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
                                     @Valid @RequestBody BookingDto booking) {
        log.info("Начало выполнения по эндпоинту @PostMapping create");
        return bookingService.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        log.info("Начало выполнения по эндпоинту @PatchMapping update");
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Начало выполнения по эндпоинту @GetMapping getBooking");
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
                                                @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Начало выполнения по эндпоинту @GetMapping {bookingId}");
        return bookingService.getBookings(userId, state);

    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsOwner(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Начало выполнения по эндпоинту @GetMapping getBookingsOwner");
        return bookingService.getBookingsOwner(userId, state);
    }
}
