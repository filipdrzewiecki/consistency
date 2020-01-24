package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConsistencyCalculations {

    <T> boolean checkIfMatchAndIncrement(T first, T second) {
        return first.equals(second);
    }

    <T extends BigDecimal> boolean checkIfDecimalsMatchAndIncrement(T first, T second) {
        return first.compareTo(second) <= 0;
    }

    BigDecimal calculatePercentageOfConsistency(Integer value, int numberOfRecords) {
        if (numberOfRecords == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(100L))
                .divide(BigDecimal.valueOf(numberOfRecords), 2, RoundingMode.HALF_UP);
    }
}
