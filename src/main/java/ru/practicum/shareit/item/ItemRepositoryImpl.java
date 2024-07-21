package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, List<Item>> items = new HashMap<>();
    private int lastItemId;

    @Override
    public Item getItem(int itemId) {
        log.info("Получение итема из репозитория. getItem({})", itemId);
        return items.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Вещи с id {}, нет", itemId);
                    return new ValidationException("Вещи нет");
                });
    }

    @Override
    public List<Item> allItems() {
        log.info("Получение всех итемов из репозитория. allItems");
        return items.values().stream()
                .flatMap(List::stream)
                .toList();

    }

    @Override
    public Item create(int userId, Item item) {
        log.info("Создание итема в репозитории. create");
        items.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        item.setId(++lastItemId);
        item.setOwner(userId);
        return item;
    }

    @Override
    public Item update(int userId, Item newItem) {
        log.info("Обновление итема в репозитории. update");
        List<Item> itemList = items.get(userId);
        if (itemList != null) {
            itemList.replaceAll(oldItem -> {
                if (oldItem.getId() == newItem.getId()) {
                    return newItem;
                }
                return oldItem;
            });
        }
        return newItem;
    }
}
