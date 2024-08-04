package ru.practicum.shareit.item;

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
import ru.practicum.shareit.http.HttpHeadersConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;


import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemCommentDto getItemById(@PathVariable int itemId) {
        log.info("Обработка запроса по эндпоинту @GetMapping getItemById");
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemCommentsDateDto> getAllItems(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) int userId) {
        log.info("Обработка запроса по эндпоинту @GetMapping getAllItems");
        return itemService.getAllItems(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) int userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Обработка запроса по эндпоинту @PostMapping create");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) int userId,
                          @PathVariable int itemId,
                          @RequestBody ItemUpdateDto itemUpdateDtoDto) {
        log.info("Обработка запроса по эндпоинту @PatchMapping update");
        return itemService.update(userId, itemId, itemUpdateDtoDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Обработка запроса по эндпоинту @GetMapping searchItems");
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) int userId,
                                    @PathVariable int itemId,
                                    @RequestBody CommentDto commentDto) {
        log.info("Обработка запроса по эндпоинту @PostMapping createComment");
        return itemService.createComment(userId, itemId, commentDto);
    }
}
