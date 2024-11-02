package org.bank.repository;

import org.bank.model.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<T> implements IRepository<T> {
    private final Map<Integer, T> data = new HashMap<>();
    private int currentId = 0;

    @Override
    public int create(T obj) {
        int newId = ++currentId;

        if (obj instanceof Identifiable) {
            ((Identifiable) obj).setId(newId);
        }
        data.put(newId, obj);
        return newId;
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

    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    private int getIdFromObj(T obj) {
        try {
            return (Integer) obj.getClass().getMethod("getId").invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting ID from object", e);
        }
    }
}
