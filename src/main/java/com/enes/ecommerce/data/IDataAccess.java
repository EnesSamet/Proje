package com.enes.ecommerce.data;

import java.util.List;

public interface IDataAccess<T> {
    void add(T item);
    void update(T item);
    void delete(int id);
    T getById(int id);
    List<T> getAll();
}
