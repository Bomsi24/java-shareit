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
import java.util.Collections;
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
        log.info("Получение списка итемов");
        Map<Integer, Item> items = itemRepository.findItemsByOwnerId(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        if (items.isEmpty()) {
            throw new ValidationException("Список вещей пуст");
        }
        log.info("Получение списка букингов");
        Map<Integer, Booking> bookings = bookingRepository.findByItemOwner_Id(userId)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));

        log.info("Получение спика комантариев");
        List<CommentDto> commentDto = CommentMapper.toCommentDtoList(
                commentRepository.findByItemIdIn(new ArrayList<>(items.keySet())));

        log.info("Возврат спика ItemCommentDateDto");
        return items.values().stream()
                .map(item -> {
                    LocalDateTime start = getTime(item, bookings);
                    LocalDateTime end = getTime(item, bookings);
                    return ItemMapper.toItemUsersDto(item, start, end, commentDto);
                })
                .toList();
    }

    private LocalDateTime getTime(Item item, Map<Integer, Booking> bookings) {
        //Получение даты и времени из букинга по itemId
        return bookings.getOrDefault(item.getId(), null) != null ?
                bookings.get(item.getId()).getStart() : null;
    }

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        log.info("Начало создание итема. id юзера:{} , итем:{}", userId, itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с id:{} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });
        itemDto.setOwner(userId);
        itemDto.setRequest("null");
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
        log.info("состав {}", itemDto);
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
