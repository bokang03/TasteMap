package com.example.TasteMap.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryDbRepository<T extends MemoryDbEntity> implements IfsMemoryDbRepository<T> {

    private final List<T> db = new ArrayList<>();
    private int id = 0;

    @Override
    public Optional<T> findById(int id) {
        return db.stream().filter(it -> it.getId() == id).findFirst();
    }

    @Override
    public T save(T entity) {
        var optionalEntity = db.stream().filter(it -> it.getId() == entity.getId()).findFirst();

        if(optionalEntity.isEmpty()){
            // db 에 데이터가 없는 경우
            id++;
            entity.setId(id);
            db.add(entity);
            return entity;

        }else{
            // db 에 이미 데이터가 있는 경우
            var preId = optionalEntity.get().getId();
            entity.setId(preId);

            deleteById(preId);
            db.add(entity);
            return entity;
        }
    }

    @Override
    public void deleteById(int id) {
        var optionalEntity = db.stream().filter(it -> it.getId() == id).findFirst();
        if(optionalEntity.isPresent()){
            db.remove(optionalEntity.get());
        }
    }

    @Override
    public List<T> findAll() {
        return db;
    }
}
