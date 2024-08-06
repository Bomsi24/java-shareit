package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.handler.BookingCreateHandler;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Букинга с id:{}, нет", bookingId);
            return new NotFoundException("Букинга с id:" + bookingId + " нет");
        });

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        log.info("bookingId:{}, ownerId:{}", bookingId, ownerId);
        UserDto bookerDto = UserMapper.toUserDto(booking.getBooker());
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());

        if (bookerId.equals(userId) || ownerId.equals(userId)) {
            return BookingMapper.toBookingResponseDto(booking, bookerDto, itemDto);
        } else {
            log.error("Юзеру с id:{}, нет доступа к информации", userId);
            throw new ValidationException("Нет доступа к информации");
        }
    }

    public BookingResponseDto create(Long userId, BookingDto booking) {
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
    public BookingResponseDto update(Long userId, Long bookingId, boolean approved) {
        log.info("Начала работы метода update Booking");
        log.info("Запускаем код на получение букинга");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Букинга с id:{}, нет", bookingId);
            return new NotFoundException("Букинга с id:" + bookingId + " нет");
        });

        Long itemId = booking.getItem().getOwner().getId();
        log.info("Запускаем проверку что пользователь явялется хозяином вещи");
        if (!itemId.equals(userId)) {
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
    public List<BookingResponseDto> getBookings(Long bookerId, String state) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            BookingCreateHandler bookingCreateHandler = new BookingCreateHandler(bookingRepository);
            List<Booking> bookings = bookingCreateHandler.retrieveBookings(null, bookerId, bookingState, sort);
            return BookingMapper.mapToBookingResponseDtoList(bookings);

        } catch (IllegalArgumentException exception) {
            log.error("Указан неправильный статус");
            throw new ValidationException("Указан неправильный статус");
        }
    }

    @Override
    public List<BookingResponseDto> getBookingsOwner(Long userId, String state) {
        log.info("Начало метода getBookingOwner");
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        if (items == null || items.isEmpty()) {
            log.info("Нет итемов");
            throw new NotFoundException("Нет итемов");
        }
        try {
            BookingState bookingState = BookingState.valueOf(state);
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            BookingCreateHandler bookingCreateHandler = new BookingCreateHandler(bookingRepository);
            List<Booking> bookings = bookingCreateHandler.retrieveBookings(items, null, bookingState, sort);
            return BookingMapper.mapToBookingResponseDtoList(bookings);

        } catch (IllegalArgumentException exception) {
            log.error("Указан неправильный статус");
            throw new ValidationException("Указан неправильный статус");
        }
    }
}
