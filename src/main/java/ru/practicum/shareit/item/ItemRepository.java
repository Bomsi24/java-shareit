package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getItem(int itemId);

    List<Item> allItems();

    Item create(int userId, Item item);

    Item update(int userId, Item newItem);
}
