package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemCommentDto getItemById(int itemId) {
        log.info("Начало получение итема по id: {}", itemId);
        log.info("Получение комментариев");
        List<CommentDto> commentDto = CommentMapper.toCommentDtoList(commentRepository.findByItemId(itemId));
        log.info("Создание ItemCommentDto");
        return ItemMapper.toItemCommentDto(itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Итем с id{} не найден", itemId);
                    return new NotFoundException("Итема с id: " + itemId + " не найден");
                }), commentDto);
    }

    @Override
    public List<ItemCommentsDateDto> getAllItems(int userId) {
        log.info("Начало получение списка всех итемов юзера с id:{}", userId);
        log.info("Получение итемов");
        Map<Integer, Item> items1 = itemRepository.findAll().stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        log.info("Все итемы: {}", items1.keySet());
        Map<Integer, Item> items = itemRepository.findItemsByOwnerId(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        if (items.isEmpty()) {
            throw new ValidationException("Список вещей пуст");
        }
        Map<Integer, Booking> bookings = bookingRepository.findAll()
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        List<ItemCommentsDateDto> itemUsersDto = new ArrayList<>();
        List<CommentDto> commentDto = CommentMapper.toCommentDtoList(commentRepository.findAll());
        log.info("Итемы юзера:{}", items.keySet());
        for (Item item : items.values()) {
            log.info("Итемы :{}", item.getId());
        }
        for(Item item : items.values()) {
            LocalDateTime start= null;
            LocalDateTime end = null;
            if(bookings.containsKey(item.getId())) {
                Booking booking = bookings.get(item.getId());
              start = booking.getStart();
              end = booking.getEnd();
            }
            List<CommentDto> commentDtoItem = commentDto.stream()
                    .filter(comment -> comment.getItemId() == item.getId())
                    .toList();
            itemUsersDto.add(ItemMapper.toItemUsersDto(
                    item, start, end, commentDtoItem));
        }

       /* for (Booking booking : bookings) {
            log.info("букинг итем: {}", booking.getItem().getId());
            if (items.containsKey(booking.getItem().getId())) {
                Item item = items.get(booking.getItem().getId());
                List<CommentDto> commentDtoItem = commentDto.stream()
                        .filter(comment -> comment.getItemId() == item.getId())
                        .toList();
                itemUsersDto.add(ItemMapper.toItemUsersDto(
                        item, booking.getStart(), booking.getEnd(), commentDtoItem));
            }
        }*/
        return itemUsersDto;
        /*Map<Integer, Booking> bookings1 = bookingRepository.findByBookingByItemsId(items.keySet()).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        log.info("bookings: {}", bookings);

        log.info("items: {}", items);
        return items.values().stream()
                .map(item -> {
                    Booking booking = bookings.get(item.getId());
                    return ItemMapper.toItemUsersDto(item, booking.getStart(), booking.getEnd());
                })
                .toList();*/




        /*Map<Integer, Booking> bookings = bookingRepository.findByBookingByItemsId(items.keySet())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        log.info("bookings: {}", bookings);
        log.info("Создание списка ItemUserDto");
        return items.values().stream()
                .map(item -> {
                    Booking booking = bookings.get(item.getId());
                    return ItemMapper.toItemUsersDto(item, booking.getStart(), booking.getEnd());
                })
                .toList();
        */
    }

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        log.info("Начало создание итема. id юзера:{} , итем:{}", userId, itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с id:{} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });
        itemDto.setOwner(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        log.info("Получен item: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemUpdateDto itemUpdateDto) {
        log.info("Начало обновления итема id:{}", itemId);
        userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с id:{} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });
        Item newItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Итем с id{} не найден", itemId);
            return new NotFoundException("Итема с id: " + itemId + " не найден");
        });
        if (newItem.getOwner().getId() != userId) {
            log.error("Указан другой пользователь");
            throw new NotFoundException("Указан другой пользователь");
        }
        if (itemUpdateDto.getName() != null && !itemUpdateDto.getName().isEmpty()) {
            log.info("Обновлено имя итема");
            newItem.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null && !itemUpdateDto.getDescription().isEmpty()) {
            log.info("Обновлено описание итема");
            newItem.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            log.info("Обновлен статус итема");
            newItem.setAvailable(itemUpdateDto.getAvailable());
        }

        log.info("Итем с id:{} успешно обновлен", itemId);
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Начало поиска итемов по тексту text:{}", text);
        if (text == null || text.isEmpty()) {
            log.info("Передан пустой текст");
            return new ArrayList<>();
        }
        List<ItemDto> itemDto = itemRepository.search(text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .toList();
        log.info("размер {}", itemDto.size());
        log.info("состав {}",itemDto);
        return itemDto;
    }

    @Override
    public CommentDto createComment(int userId, int itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Отсутствует автор с id:{}", userId);
            return new ValidationException("Отсутсвует автор с id: " + userId);
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Отсутсвует вещь с id:{}", itemId);
            return new NotFoundException("Отсутсвует вещь с id: " + itemId);
        });
        Booking booking = bookingRepository.findByBookerId(userId).orElseThrow(() -> {
            log.error("У автора с id:{}, нет букинга", userId);
            return new NotFoundException("У автора нет букинга");
        });

        if (booking.getStart().isAfter(LocalDateTime.now()) || booking.getEnd().isAfter(LocalDateTime.now())) {
            log.error("Срок аренды вещи еще не окончен");
            throw new ValidationException("Срок аренды вещи еще не окончен");
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, author, item));
        return CommentMapper.toCommentDto(comment);
    }
}
