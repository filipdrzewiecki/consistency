package utils;

import dto.Item;
import dto.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;


import static generator.ItemGenerator.createListOfItems;
import static generator.ItemGenerator.createListOfModifiedItems;
import static generator.ItemGenerator.createListOfModifiedValuesItems;

@ExtendWith(MockitoExtension.class)
class ConsistencyUtilsTest {

    @InjectMocks
    private ConsistencyUtils consistencyUtils;

    Map<Key, Item> itemsToWrite;
    Map<Key, Item> itemsToCompare;
    ConsistencySpec<Key, Item> spec;

    @BeforeEach
    void setup() {
        List<Item> items = createListOfItems(100);
        List<Item> addItemsToCompare1 = createListOfModifiedItems(11, 21, 2);
        List<Item> addItemsToCompare2 = createListOfModifiedItems(21, 36, 3);
        List<Item> addItemsToCompare3 = createListOfModifiedValuesItems(36, 56, 2);
        List<Item> addItemsToCompare4 = createListOfModifiedValuesItems(56, 100, 3);

        List<Item> completeListToCompare = new ArrayList<>(createListOfItems(11));
        completeListToCompare.addAll(addItemsToCompare1);
        completeListToCompare.addAll(addItemsToCompare2);
        completeListToCompare.addAll(addItemsToCompare3);
        completeListToCompare.addAll(addItemsToCompare4);

        itemsToWrite = items.stream().collect(Collectors.toMap(Item::getKey, v -> v));
        itemsToCompare = completeListToCompare.stream().collect(Collectors.toMap(Item::getKey, v -> v));
        spec = new ConsistencySpec<>();
        spec.setItemsToWrite(itemsToWrite);
        spec.setItemsToCompare(itemsToCompare);
        spec.setFieldsToIgnore(List.of());
    }


    @Test
    void whenPassMapsWithKeysThenReturnConsistency() {
        //GIVEN

        //WHEN
        ConsistencyResult result = consistencyUtils.getConsistency(spec);

        //THEN
        assertThat(result.getData()).isEqualTo(BigDecimal.valueOf(10500000));
        assertThat(result.getFields()).isEqualTo(BigDecimal.valueOf(10500000));


    }
}