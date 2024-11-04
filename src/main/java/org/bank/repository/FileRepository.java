package org.bank.repository;

import org.bank.model.Identifiable;

public class FileRepository<T extends Identifiable> implements IRepository<T>{
    @Override
    public int create(T obj) {
        // TODO
        return 0;
    }

    @Override
    public T read(int id) {
        return null;
    }

    @Override
    public void update(T obj) {
        // TODO
    }

    @Override
    public void delete(int id) {
        // TODO
    }
}
