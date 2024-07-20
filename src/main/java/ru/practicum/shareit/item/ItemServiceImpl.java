package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(int itemId) {
        log.info("Начало получение итема по id: {}", itemId);
        return ItemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemUsersDto> getAllItems(int userId) {
        log.info("Начало получение списка всех итемов юзера с id:{}", userId);
        return itemRepository.allItems().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemUsersDto)
                .toList();
    }

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        log.info("Начало создание итема. id юзера:{} , итем:{}", userId, itemDto);
        if (!userRepository.userExists(userId)) {
            log.error("Пользователь с id:{} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        itemDto.setOwner(userId);
        Item item = itemRepository.create(userId, ItemMapper.toItem(itemDto));
        log.info("Получен item: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemUpdateDto itemUpdateDto) {
        log.info("Начало обновления итема id:{}", itemId);
        if (!userRepository.userExists(userId)) {
            log.error("Пользователь с id:{} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        Item newItem = itemRepository.getItem(itemId);
        if (newItem.getOwner() != userId) {
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
        return ItemMapper.toItemDto(itemRepository.update(userId, newItem));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Начало поиска итемов по тексту text:{}", text);
        if (text == null || text.isEmpty()) {
            log.info("Передан пустой текст");
            return new ArrayList<>();
        }
        return itemRepository.allItems().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
