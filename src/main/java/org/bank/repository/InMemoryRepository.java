package org.bank.repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<T> implements IRepository<T> {
    private final Map<Integer, T> data = new HashMap<>();
    private int currentId = 0;

    @Override
    public void create(T obj) {
        data.put(++currentId, obj);
    }

    @Override
    public T read(int id) {
        return data.get(id);
    }

    @Override
    public void update(T obj) throws RuntimeException{
        int id = getIdFromObj(obj);
        if (data.containsKey(id)) {
            data.put(id, obj);
        } else {
            throw new RuntimeException("Invalid update");
        }
    }

    @Override
    public void delete(int id) {
        data.remove(id);
        // TODO logica
    }

    private int getIdFromObj(T obj) {
        try {
            return (Integer) obj.getClass().getMethod("getId").invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting ID from object", e);
        }
    }
}
