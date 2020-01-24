package utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class StaticDataProvider {

    static final String RECORDS_PAIRED = "RecordsPaired";
    static final String AVERAGE_CONSISTENCY = "AverageConsistency";
    static final String TO_WRITE_AMOUNT = "ItemsToWriteNumber";
    static final String TO_COMPARE_AMOUNT = "ItemsToCompareNumber";

    static AtomicInteger numberOfRecordsToWrite = null;
    static AtomicInteger numberOfRecordsToCompare = null;
    static AtomicInteger recordsPaired = null;

    static Map<String, BigDecimal> fields = null;
    static Map<String, BigDecimal> data = null;

    static Map<String, Integer> fieldMap = null;
    static boolean areFieldsInitialized = false;
    static List<String> excludedFields = new ArrayList<>(Arrays.asList(RECORDS_PAIRED, AVERAGE_CONSISTENCY));
    
}
