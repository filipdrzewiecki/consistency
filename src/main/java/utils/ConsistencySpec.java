package utils;

import java.util.List;
import java.util.Map;

public class ConsistencySpec<Key, T>{

    private Map<Key, T> itemsToWrite;
    private Map<Key, T> itemsToCompare;
    private List<String> fieldsToIgnore;

    public ConsistencySpec() {
    }

    public ConsistencySpec(Map<Key, T> itemsToWrite, Map<Key, T> itemsToCompare, List<String> fieldsToIgnore) {
        this.itemsToWrite = itemsToWrite;
        this.itemsToCompare = itemsToCompare;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    public Map<Key, T> getItemsToWrite() {
        return itemsToWrite;
    }

    public void setItemsToWrite(Map<Key, T> itemsToWrite) {
        this.itemsToWrite = itemsToWrite;
    }

    public Map<Key, T> getItemsToCompare() {
        return itemsToCompare;
    }

    public void setItemsToCompare(Map<Key, T> itemsToCompare) {
        this.itemsToCompare = itemsToCompare;
    }

    public List<String> getFieldsToIgnore() {
        return fieldsToIgnore;
    }

    public void setFieldsToIgnore(List<String> fieldsToIgnore) {
        this.fieldsToIgnore = fieldsToIgnore;
    }
}
