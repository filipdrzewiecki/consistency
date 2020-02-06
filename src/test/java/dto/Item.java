package dto;

import java.math.BigDecimal;

public class Item {

    private String name;
    private String year;
    private String code;

    private String stringValue1;
    private BigDecimal decimalValue1;
    private BigDecimal decimalValue2;
    private Long longValue1;
    private Long longValue2;
    private long primitiveLongValue;
    private Integer integerValue1;

    public Key getKey() {
        Key key = new Key();
        key.setCode(this.code);
        key.setName(this.name);
        key.setYear(this.year);
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStringValue1() {
        return stringValue1;
    }

    public void setStringValue1(String stringValue1) {
        this.stringValue1 = stringValue1;
    }

    public BigDecimal getDecimalValue1() {
        return decimalValue1;
    }

    public void setDecimalValue1(BigDecimal decimalValue1) {
        this.decimalValue1 = decimalValue1;
    }

    public BigDecimal getDecimalValue2() {
        return decimalValue2;
    }

    public void setDecimalValue2(BigDecimal decimalValue2) {
        this.decimalValue2 = decimalValue2;
    }

    public Long getLongValue1() {
        return longValue1;
    }

    public void setLongValue1(Long longValue1) {
        this.longValue1 = longValue1;
    }

    public Long getLongValue2() {
        return longValue2;
    }

    public void setLongValue2(Long longValue2) {
        this.longValue2 = longValue2;
    }

    public long getPrimitiveLongValue() {
        return primitiveLongValue;
    }

    public void setPrimitiveLongValue(long primitiveLongValue) {
        this.primitiveLongValue = primitiveLongValue;
    }

    public Integer getIntegerValue1() {
        return integerValue1;
    }

    public void setIntegerValue1(Integer integerValue1) {
        this.integerValue1 = integerValue1;
    }
}
