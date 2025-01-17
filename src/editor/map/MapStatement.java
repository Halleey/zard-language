package editor.map;

import java.util.HashMap;
import java.util.Map;


public class MapStatement<K, V> {
    private Map<K, V> objectMap;

    public MapStatement() {
        this.objectMap = new HashMap<>();
    }

    public MapStatement(Map<K, V> objectMap) {
        this.objectMap = objectMap;
    }

    public void put(K key, V value) {
        objectMap.put(key, value);
    }

    public V get(K key) {
        if (!objectMap.containsKey(key)) {
            throw new IllegalArgumentException("Chave não encontrada: " + key);
        }
        return objectMap.get(key);
    }

    public V remove(K key) {
        if (!objectMap.containsKey(key)) {
            throw new IllegalArgumentException("Chave não encontrada: " + key);
        }
        return objectMap.remove(key);
    }

    public boolean containsKey(K key) {
        return objectMap.containsKey(key);
    }

    public boolean containsValue(V value) {
        return objectMap.containsValue(value);
    }

    public void clear() {
        objectMap.clear();
    }

    public int size() {
        return objectMap.size();
    }

    public void setObjectMap(Map<K, V> objectMap) {
        this.objectMap = objectMap;
    }

    public Map<K, V> getObjectMap() {
        return objectMap;
    }

    @Override
    public String toString() {
        return objectMap.toString();
    }
}
