package com.example.TasteMap.repository;

import com.example.TasteMap.domain.TasteMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasteMapRepository extends JpaRepository<TasteMapEntity, Integer> {
}
