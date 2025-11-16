package com.example.TasteMap.repository;

import com.example.TasteMap.db.MemoryDbRepository;
import com.example.TasteMap.domain.TasteMapEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TasteMapRepository extends MemoryDbRepository<TasteMapEntity> {
}
