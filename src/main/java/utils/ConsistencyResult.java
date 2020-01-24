package utils;

import java.math.BigDecimal;
import java.util.Map;

public class ConsistencyResult {

    private Map<String, BigDecimal> fields;
    private Map<String, BigDecimal> data;

    public ConsistencyResult(Map<String, BigDecimal> fields, Map<String, BigDecimal> data) {
        this.fields = fields;
        this.data = data;
    }

    public Map<String, BigDecimal> getFields() {
        return fields;
    }

    public void setFields(Map<String, BigDecimal> fields) {
        this.fields = fields;
    }

    public Map<String, BigDecimal> getData() {
        return data;
    }

    public void setData(Map<String, BigDecimal> data) {
        this.data = data;
    }
}
