package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemCommentDto getItemById(Long itemId);

    List<ItemCommentsDateDto> getAllItems(Long userId);

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
