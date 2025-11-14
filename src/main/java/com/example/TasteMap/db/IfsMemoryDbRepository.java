package com.example.TasteMap.db;

import java.util.List;
import java.util.Optional;

public interface IfsMemoryDbRepository<T> {

    Optional<T> findById(int id);
    T save(T entity);
    void deleteById(int id);
    List<T> findAll();
}
