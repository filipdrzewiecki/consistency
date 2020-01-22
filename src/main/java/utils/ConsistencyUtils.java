package utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class ConsistencyUtils {

    private AtomicInteger numberOfRecords = null;
    private AtomicInteger recordsPaired = null;
    private AtomicInteger totalItems = null;
    private Map<String, BigDecimal> dbConsistency = null;
    private Map<String, Integer> CONSISTENCY_MAP = null;
    private boolean areFieldsInitialized = false;

    public <Key, T> Map<String, BigDecimal> getConsistency(Map<Key, T> itemsToWrite, Map<Key, T> itemsToCompare) {
        initializeGlobalVariables();
        mapClassToFieldMap(itemsToWrite, itemsToCompare);

        totalItems.getAndAdd(itemsToWrite.size());

        setRecordsPaired();

        fillTheConsistencyMap(CONSISTENCY_MAP, numberOfRecords.get(), dbConsistency);

        setAverageConsistency();

        return dbConsistency;
    }

    private <T> boolean checkIfMatchAndIncrement(T first, T second) {
        return first.equals(second);
    }

    private <T extends BigDecimal> boolean checkIfDecimalsMatchAndIncrement(T first, T second) {
        BigDecimal absoluteDifferenceValue = first.subtract(second).abs();
        return isTheSameWithTolerance(absoluteDifferenceValue);

    }

    private void fillTheConsistencyMap(Map<String, Integer> fieldMap, int numberOfRecords, Map<String, BigDecimal> dbConsistency) {
        Set<String> names = fieldMap.keySet();
        for (String name : names) {
            dbConsistency.put(name, calculatePercentageOfConsistency2(fieldMap.get(name), numberOfRecords));
        }
    }

    private <T extends BigDecimal> boolean isTheSameWithTolerance(T value) {
        return value.compareTo(BigDecimal.ONE) <= 0;
    }


    private BigDecimal calculatePercentageOfConsistency2(Integer value, int numberOfRecords) {
        if (numberOfRecords == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(100L))
                .divide(BigDecimal.valueOf(numberOfRecords), 2, RoundingMode.HALF_UP);
    }

    private void initializeMapFields(Class type) {
        if (CONSISTENCY_MAP == null) {
            Field[] allFields = type.getDeclaredFields();
            Map<String, Integer> consistencyMap = new HashMap<>();
            for (Field field : allFields) {
                consistencyMap.put(field.getName(), 0);
            }
            CONSISTENCY_MAP = consistencyMap;
        }
    }

    private BigDecimal getAverageSimilarity(Map<String, BigDecimal> consistencyValues) {
        BigDecimal sum = consistencyValues.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal number = BigDecimal.valueOf(consistencyValues.size());
        return sum.divide(number, 2, RoundingMode.HALF_UP);
    }

    private <T> void compareObjects(T objectToWrite, T objectToCompare) {
        Class<?> objectClass = objectToWrite.getClass();
        Field[] allFields = objectClass.getDeclaredFields();
        try {
            for (Field field : allFields) {
                field.setAccessible(true);

                Class fieldType = field.getType();
                Object fieldValueToWrite = field.get(objectToWrite);
                Object fieldValueToCompare = field.get(objectToCompare);
                String fieldName = field.getName();

                boolean doFieldValuesMatch = doFieldValuesMatch(fieldValueToWrite, fieldValueToCompare, fieldType);
                ifFieldValuesMatchThenIncrementConsistency(fieldName, doFieldValuesMatch);
            }
        } catch (IllegalAccessException e) {

        }
    }

    private boolean doFieldValuesMatch (Object fieldValueToWrite, Object fieldValueToCompare, Class fieldType) {
        boolean doFieldValuesMatch = false;
        if (fieldType == BigDecimal.class) {
            doFieldValuesMatch = checkIfDecimalsMatchAndIncrement((BigDecimal) fieldValueToWrite, (BigDecimal) fieldValueToCompare);
        }
        if (fieldType == String.class) {
            doFieldValuesMatch = checkIfMatchAndIncrement(fieldValueToWrite, fieldValueToCompare);
        }
        if (fieldType == Long.class) {
            doFieldValuesMatch = checkIfMatchAndIncrement(fieldValueToWrite, fieldValueToCompare);
        }
        return doFieldValuesMatch;
    }

    private void ifFieldValuesMatchThenIncrementConsistency (String fieldName, boolean doFieldValuesMatch) {
        Integer counter = CONSISTENCY_MAP.get(fieldName);
        if (doFieldValuesMatch) {
            CONSISTENCY_MAP.put(fieldName, counter + 1);
        }
    }

    private void initializeGlobalVariables() {
        if (!areFieldsInitialized) {
            numberOfRecords = new AtomicInteger(0);
            recordsPaired = new AtomicInteger(0);
            totalItems = new AtomicInteger(0);
            dbConsistency = new HashMap<>();
            areFieldsInitialized = true;
        }
    }

    private <Key, T> void mapClassToFieldMap(Map<Key, T> itemsToWrite, Map<Key, T> itemsToCompare) {
        Class itemClass = itemsToWrite.values().stream().findFirst().get().getClass();
        initializeMapFields(itemClass);
        for (Key key : itemsToWrite.keySet()) {
            T itemToWrite = itemsToWrite.get(key);
            T itemToCompare = itemsToCompare.get(key);
            if (itemToCompare != null) {
                compareObjects(itemToWrite, itemToCompare);
                recordsPaired.getAndIncrement();
            }
        }
    }

    private void setRecordsPaired() {
        numberOfRecords.set(recordsPaired.get());

        BigDecimal one = BigDecimal.valueOf(numberOfRecords.get());
        BigDecimal two = BigDecimal.valueOf(totalItems.get());
        BigDecimal recordPairedPercentage = one.divide(two, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L));
        dbConsistency.put("RecordsPaired", recordPairedPercentage);
    }

    private void setAverageConsistency() {
        BigDecimal averageConsistency = getAverageSimilarity(dbConsistency);
        dbConsistency.put("AverageConsistency", averageConsistency);
    }
}
