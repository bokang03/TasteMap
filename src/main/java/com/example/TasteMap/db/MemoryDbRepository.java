package com.example.TasteMap.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryDbRepository<T extends MemoryDbEntity> implements IfsMemoryDbRepository<T> {

    private final List<T> db = new ArrayList<>();
    private int id = 0;

    @Override
    public Optional<T> findById(int id) {
        return db.stream()
                .filter(it -> Integer.valueOf(id).equals(it.getId()))
                .findFirst();
    }

    @Override
    public T save(T entity) {
        if (isNew(entity)) return createNew(entity);

        var existing = findByEntityId(entity);
        return existing
                .map(e -> replaceExisting(e.getId(), entity))
                .orElseGet(() -> createNew(entity));
    }

    private boolean isNew(T entity) {
        return entity.getId() == null;
    }

    private Optional<T> findByEntityId(T entity) {
        return db.stream()
                .filter(it -> it.getId() != null && it.getId().equals(entity.getId()))
                .findFirst();
    }

    private T createNew(T entity) {
        id++;
        entity.setId(id);
        db.add(entity);
        return entity;
    }

    private T replaceExisting(Integer preId, T entity) {
        entity.setId(preId);
        deleteById(preId);
        db.add(entity);
        return entity;
    }

    @Override
    public void deleteById(int id) {
        var optionalEntity = db.stream()
                .filter(it -> Integer.valueOf(id).equals(it.getId()))
                .findFirst();
        if (optionalEntity.isEmpty()) return;
        db.remove(optionalEntity.get());
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(db);
    }
}
