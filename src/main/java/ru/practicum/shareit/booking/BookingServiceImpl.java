package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto getBooking(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Букинга с id:{}, нет", bookingId);
            return new NotFoundException("Букинга с id:" + bookingId + " нет");
        });

        int bookerId = booking.getBooker().getId();
        int ownerId = booking.getItem().getOwner().getId();
        log.info("bookingId:{}, ownerId:{}", bookingId, ownerId);
        UserDto bookerDto = UserMapper.toUserDto(booking.getBooker());
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());

        if (bookerId == userId || ownerId == userId) {
            return BookingMapper.toBookingResponseDto(booking, bookerDto, itemDto);
        } else {
            log.error("Юзеру с id:{}, нет доступа к информации", userId);
            throw new ValidationException("Нет доступа к информации");
        }
    }

    public BookingResponseDto create(int userId, BookingDto booking) {
        log.info("Букинг {}", booking);
        log.info("Получение итема из букинга");
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> {
            log.error("Item not found");
            return new NotFoundException("Item not found");
        });

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found");
            return new InternalServerException("User not found");
        });

        if (!item.getAvailable()) {
            throw new ValidationException("Товар недоступен");
        }
        log.info("Получение юзера из букинга");
        log.info("Букинк id{}", booking.getBookerId());
        User owner = item.getOwner();
        if (owner == null) {
            log.error("User not found");
            throw new InternalServerException("User not found");
        }

        booking.setStatus(BookingStatus.WAITING.name());
        log.info("Создание букинга{}", booking);
        Booking bookingApproved = BookingMapper.toBooking(
                booking, item, user);
        Booking newBooking = bookingRepository.save(bookingApproved);
        log.info("делаем Dto");
        return BookingMapper.toBookingResponseDto(
                newBooking, UserMapper.toUserDto(user), ItemMapper.toItemDto(item)
        );
    }

    @Override
    public BookingResponseDto update(int userId, int bookingId, boolean approved) {
        log.info("Начала работы метода update Booking");
        log.info("Запускаем метод на проверку юзера");
        log.info("Запускаем код на получение букинга");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Букинга с id:{}, нет", bookingId);
            return new NotFoundException("Букинга с id:" + bookingId + " нет");
        });

        int itemId = booking.getItem().getOwner().getId();
        log.info("Запускаем проверку что пользователь явялется хозяином вещи");
        if (itemId != userId) {
            log.error("Юзер c id: {}, не является хозяином вещи с id: {}", itemId, userId);
            throw new ValidationException("Неправильный хозяин вещи");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Сохраняем измененный букинг");
        bookingRepository.save(booking);
        ItemDto item = ItemMapper.toItemDto(booking.getItem());
        UserDto booker = UserMapper.toUserDto(booking.getBooker());
        return BookingMapper.toBookingResponseDto(booking, booker, item);
    }

    @Override
    public List<BookingResponseDto> getBookings(int bookerId, String state) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            List<Booking> bookings = retrieveBookingsByBookerId(bookerId, bookingState, sort);
            return BookingMapper.mapToBookingResponseDtoList(bookings);

        } catch (IllegalArgumentException exception) {
            log.error("Указан неправильный статус");
            throw new ValidationException("Указан неправильный статус");
        }

    }

    @Override
    public List<BookingResponseDto> getBookingsOwner(int userId, String state) {
        log.info("Начало метода getBookingOwner");
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        if (items == null || items.isEmpty()) {
            log.info("Нет итемов");
            throw new NotFoundException("Нет итемов");
        }
        try {
            BookingState bookingState = BookingState.valueOf(state);
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            List<Booking> bookings = retrieveBookingsByItems(items, bookingState, sort);
            return BookingMapper.mapToBookingResponseDtoList(bookings);

        } catch (IllegalArgumentException exception) {
            log.error("Указан неправильный статус");
            throw new ValidationException("Указан неправильный статус");
        }
    }

    private List<Booking> retrieveBookingsByItems(List<Item> items, BookingState bookingState, Sort sort) {
        return switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemIn(items, sort);
            case PAST -> bookingRepository.findAllByItemInAndEndIsBefore(items, LocalDateTime.now(), sort);
            case CURRENT -> bookingRepository.findRelevantBookingsByItem(items, LocalDateTime.now(), sort);
            case FUTURE -> bookingRepository.findAllByItemInAndEndAfter(items, LocalDateTime.now(), sort);
            case WAITING -> bookingRepository.findAllByItemInAndStatus(items, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findAllByItemInAndStatus(items, BookingStatus.REJECTED, sort);
        };
    }

    private List<Booking> retrieveBookingsByBookerId(int bookerId, BookingState bookingState, Sort sort) {
        return switch (bookingState) {
            case ALL -> bookingRepository.findAllByBookerId(bookerId, sort);
            case PAST -> bookingRepository.findByBookerIdAndEndIsBefore(bookerId, LocalDateTime.now(), sort);
            case CURRENT -> bookingRepository.findRelevantBookingsByBookingId(bookerId, LocalDateTime.now(), sort);
            case FUTURE -> bookingRepository.findByBookerIdAndEndAfter(bookerId, LocalDateTime.now(), sort);
            case WAITING -> bookingRepository.findBookingByIdAndStatus(bookerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findBookingByIdAndStatus(bookerId, BookingStatus.REJECTED, sort);
        };
    }
}
