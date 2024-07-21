package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(int itemId);

    List<ItemUsersDto> getAllItems(int userId);

    ItemDto create(int userId, ItemDto item);

    ItemDto update(int userId, int itemId, ItemUpdateDto itemUpdateDto);

    List<ItemDto> searchItems(String text);
}
