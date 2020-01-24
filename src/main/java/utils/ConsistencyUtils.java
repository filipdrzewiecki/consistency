package utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsistencyUtils {

    private static final Logger log =  Logger.getLogger(ConsistencyUtils.class.getSimpleName());

    private ConsistencyCalculations calculator = new ConsistencyCalculations();

    private static final String RECORDS_PAIRED = "RecordsPaired";
    private static final String AVERAGE_CONSISTENCY = "AverageConsistency";
    private static final String TO_WRITE_AMOUNT = "ItemsToWriteNumber";
    private static final String TO_COMPARE_AMOUNT = "ItemsToCompareNumber";

    private AtomicInteger numberOfRecordsToWrite = null;
    private AtomicInteger numberOfRecordsToCompare = null;
    private AtomicInteger recordsPaired = null;

    private Map<String, BigDecimal> fields = null;
    private Map<String, BigDecimal> data = null;

    private Map<String, Integer> fieldMap = null;
    private boolean areFieldsInitialized = false;
    private List<String> excludedFields = new ArrayList<>(Arrays.asList(RECORDS_PAIRED, AVERAGE_CONSISTENCY));

    public <Key, T> ConsistencyResult getConsistency(ConsistencySpec spec) {
        Map itemsToWrite = spec.getItemsToWrite();
        Map itemsToCompare = spec.getItemsToCompare();
        excludedFields.addAll(spec.getFieldsToIgnore());
        return getConsistency(itemsToWrite, itemsToCompare);
    }

    public <Key, T> ConsistencyResult getConsistency(Map<Key, T> itemsToWrite, Map<Key, T> itemsToCompare) {
        initializeDataAndFields(itemsToWrite, itemsToCompare);
        mapFields(itemsToWrite, itemsToCompare);
        numberOfRecordsToWrite.getAndAdd(itemsToWrite.size());
        fillFieldMap();
        fillDataMap();
        return getConsistencyResult();
    }

    private void fillFieldMap() {
        Set<String> names = fieldMap.keySet();
        for (String name : names) {
            BigDecimal value = calculator.calculatePercentageOfConsistency(fieldMap.get(name), recordsPaired.get());
            fields.put(name, value);
        }
    }

    private void initializeMapFields(Class type) {
        if (fieldMap == null) {
            Field[] allFields = type.getDeclaredFields();
            Map<String, Integer> consistencyMap = new HashMap<>();
            for (Field field : allFields) {
                if (!excludedFields.contains(field.getName())) {
                    consistencyMap.put(field.getName(), 0);
                }
            }
            fieldMap = consistencyMap;
        }
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
                if (!excludedFields.contains(fieldName)) {
                    boolean doFieldValuesMatch = doFieldValuesMatch(fieldValueToWrite, fieldValueToCompare, fieldType);
                    ifFieldValuesMatchThenIncrementConsistency(fieldName, doFieldValuesMatch);
                }
            }
        } catch (IllegalAccessException e) {
            log.info("Field doesn't exist" + e);
        }
    }

    private boolean doFieldValuesMatch(Object fieldValueToWrite, Object fieldValueToCompare, Class fieldType) {
        boolean doFieldValuesMatch = false;
        if (fieldType == BigDecimal.class) {
            doFieldValuesMatch = calculator.checkIfDecimalsMatchAndIncrement((BigDecimal) fieldValueToWrite, (BigDecimal) fieldValueToCompare);
        }
        if (fieldType == String.class) {
            doFieldValuesMatch = calculator.checkIfMatchAndIncrement(fieldValueToWrite, fieldValueToCompare);
        }
        if (fieldType == Long.class) {
            doFieldValuesMatch = calculator.checkIfMatchAndIncrement(fieldValueToWrite, fieldValueToCompare);
        }
        return doFieldValuesMatch;
    }

    private void ifFieldValuesMatchThenIncrementConsistency(String fieldName, boolean doFieldValuesMatch) {
        Integer counter = fieldMap.get(fieldName);
        if (doFieldValuesMatch) {
            fieldMap.put(fieldName, counter + 1);
        }
    }

    private void initializeGlobalVariables() {
        numberOfRecordsToWrite = new AtomicInteger(0);
        recordsPaired = new AtomicInteger(0);
        numberOfRecordsToWrite = new AtomicInteger(0);
        numberOfRecordsToCompare = new AtomicInteger(0);
        data = new HashMap<>();
        fields = new HashMap<>();
    }

    private <Key, T> void mapFields(Map<Key, T> itemsToWrite, Map<Key, T> itemsToCompare) {
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

    private void fillDataMap() {
        setRecordsPaired();
        setItemsAmounts();
        setAverageSimilarity();
    }

    private void setAverageSimilarity() {
        Map<String, BigDecimal> consistencyCopy = new HashMap<>(fields);
        BigDecimal sum = consistencyCopy.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal number = BigDecimal.valueOf(consistencyCopy.size());
        BigDecimal averageConsistency = sum.divide(number, 2, RoundingMode.HALF_UP);
        data.put(AVERAGE_CONSISTENCY, averageConsistency);

    }

    private void setRecordsPaired() {
        BigDecimal one = BigDecimal.valueOf(recordsPaired.get());
        BigDecimal two = BigDecimal.valueOf(numberOfRecordsToWrite.get());
        BigDecimal recordPairedPercentage = one.divide(two, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L));
        data.put(RECORDS_PAIRED, recordPairedPercentage);
    }

    private void setItemsAmounts() {
        BigDecimal toWriteAmount = BigDecimal.valueOf(numberOfRecordsToWrite.get());
        BigDecimal toCompareAmount = BigDecimal.valueOf(numberOfRecordsToCompare.get());
        data.put(TO_WRITE_AMOUNT, toWriteAmount);
        data.put(TO_COMPARE_AMOUNT, toCompareAmount);
    }

    private void excludeKeyFields(Class key) {
        Field[] allFields = key.getDeclaredFields();
        for (Field field : allFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            excludedFields.add(fieldName);
        }
    }

    private ConsistencyResult getConsistencyResult() {
        return new ConsistencyResult(fields, data);
    }

    private <Key, T> void initializeDataAndFields(Map<Key, T> itemsToWrite, Map<Key, T> itemsToCompare) {
        if (!areFieldsInitialized) {
            initializeGlobalVariables();
            numberOfRecordsToCompare.getAndAdd(itemsToCompare.size());
            excludeKeyFields(itemsToWrite.keySet().stream().findFirst().get().getClass());
            areFieldsInitialized = true;
        }
    }
}
