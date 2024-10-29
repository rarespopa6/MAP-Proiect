package org.bank.repository;

public interface IRepository<T> {
    int create(T obj);
    T read (int id);
    void update(T obj);
    void delete(int id);
}
