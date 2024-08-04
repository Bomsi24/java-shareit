package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Builder
public class ItemCommentDto {
    private int id;
    private String name;
    private String description;
    private User lastBooking;
    private User nextBooking;
    private Boolean available;
    private List<CommentDto> comments;
}
