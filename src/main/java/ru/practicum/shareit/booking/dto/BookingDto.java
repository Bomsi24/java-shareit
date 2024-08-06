package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private String status;
}
