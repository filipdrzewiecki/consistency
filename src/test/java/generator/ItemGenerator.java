package generator;

import dto.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ItemGenerator {

    public static Item createItem(int id) {
        long value = 1000 * id;
        long value2 = 1000 + id;
        BigDecimal decimalValue = BigDecimal.valueOf(value);
        BigDecimal decimalValue2 = BigDecimal.valueOf(value2);

        Item item = new Item();
        item.setName("name" + id);
        item.setCode("code" + id);
        item.setYear("2020");
        item.setDecimalValue1(decimalValue);
        item.setDecimalValue2(decimalValue2);
        item.setLongValue1(value);
        item.setLongValue2(value2);
        item.setStringValue1("StringValue" + id);
        item.setPrimitiveLongValue(value);
        item.setIntegerValue1((int) value2);

        return item;
    }

    public static Item modifyItemValues(Item item, int modifier) {
        BigDecimal decimalValue1 = item.getDecimalValue1();
        BigDecimal decimalValue2 = item.getDecimalValue2();
        Long longValue1 = item.getLongValue1();
        Long longValue2 = item.getLongValue2();

        item.setDecimalValue1(decimalValue1.add(BigDecimal.valueOf(modifier)));
        item.setDecimalValue2(decimalValue2.add(BigDecimal.valueOf(modifier)));
        item.setLongValue1(longValue1 + modifier);
        item.setLongValue2(longValue2 + modifier);

        return item;
    }

    public static Item modifyOtherItemValues(Item item, int modifier) {
        String stringValue = item.getStringValue1();
        long primitiveLongValue = item.getPrimitiveLongValue();
        Integer integerValue1 = item.getIntegerValue1();

        item.setStringValue1(stringValue + modifier);
        item.setPrimitiveLongValue(primitiveLongValue + modifier);
        item.setIntegerValue1(integerValue1 + modifier);

        return item;
    }

    public static List<Item> createListOfItems (int id) {
        final var items = IntStream.range(1, 32)
                .mapToObj(i -> createItem(i))
                .collect(Collectors.toUnmodifiableList());

        List<Item> list = new ArrayList<>();

        list.addAll(items);

        return list;
    }

    public static List<Item> createListOfModifiedItems(int id) {
        final var listToCompare1 = IntStream.range(1, 11)
                .mapToObj(i -> createItem(i))
                .collect(Collectors.toList());

        final var listToCompare2  = IntStream.range(11, 21)
                .mapToObj(i -> modifyItemValues(createItem(i), i))
                .collect(Collectors.toList());

        final var listToCompare3  = IntStream.range(21, 31)
                .mapToObj(i -> modifyOtherItemValues(modifyItemValues(createItem(i), i), i))
                .collect(Collectors.toList());

        List<Item> modifiedList = new ArrayList<>();

        modifiedList.addAll(listToCompare1);
        modifiedList.addAll(listToCompare2);
        modifiedList.addAll(listToCompare3);

        return modifiedList;
    }
}
