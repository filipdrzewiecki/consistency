package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

class ConsistencyCalculations {

    BigDecimal calculatePercentageOfConsistency(Integer value, int numberOfRecords) {
        if (numberOfRecords == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(100L))
                .divide(BigDecimal.valueOf(numberOfRecords), 2, RoundingMode.HALF_UP);
    }

    boolean doFieldValuesMatch(Object fieldValueToWrite, Object fieldValueToCompare, Class fieldType) {
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

    private <T> boolean checkIfMatchAndIncrement(T first, T second) {
        return first.equals(second);
    }

    private <T extends BigDecimal> boolean checkIfDecimalsMatchAndIncrement(T first, T second) {
        return first.compareTo(second) <= 0;
    }
}
