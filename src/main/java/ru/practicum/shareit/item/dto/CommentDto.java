package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private int id;
    @NotNull
    @NotBlank
    private String text;
    @NotNull
    private int itemId;
    @NotNull
    private String authorName;
    private LocalDateTime created;
}
