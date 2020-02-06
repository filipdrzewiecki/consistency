package utils;

import dto.Item;
import dto.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static generator.ItemGenerator.createListOfItems;
import static generator.ItemGenerator.createListOfModifiedItems;

@ExtendWith(MockitoExtension.class)
class ConsistencyUtilsTest {

    @InjectMocks
    private ConsistencyUtils consistencyUtils;

    @BeforeEach
    void setup() {

    }


    @Test
    void whenPassMapsWithKeysThenReturnConsistency() {
        List<Item> items = createListOfItems(100);
        List<Item> itemsModified = createListOfModifiedItems(100);

        Map<Key, Item> itemsToWrite = items.stream().collect(Collectors.toMap(Item::getKey, v -> v));
        Map<Key, Item> itemsToCompare = itemsModified.stream().collect(Collectors.toMap(Item::getKey, v -> v));
        ConsistencySpec spec;
    }
}