package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemCommentDto getItemById(int itemId);

    List<ItemCommentsDateDto> getAllItems(int userId);

    ItemDto create(int userId, ItemDto item);

    ItemDto update(int userId, int itemId, ItemUpdateDto itemUpdateDto);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(int userId, int itemId, CommentDto commentDto);
}
