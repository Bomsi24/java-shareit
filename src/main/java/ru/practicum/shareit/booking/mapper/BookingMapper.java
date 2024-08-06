package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.valueOf(bookingDto.getStatus()))
                .build();
    }

    public static List<BookingResponseDto> mapToBookingResponseDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> {
                    ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
                    UserDto userDto = UserMapper.toUserDto(booking.getBooker());
                    return toBookingResponseDto(booking, userDto, itemDto);
                })
                .toList();
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking, UserDto userDto, ItemDto itemDto) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart().toString())
                .end(booking.getEnd().toString())
                .status(booking.getStatus().name())
                .booker(userDto)
                .item(itemDto)
                .build();
    }
}
