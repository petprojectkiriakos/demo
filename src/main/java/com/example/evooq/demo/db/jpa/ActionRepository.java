package com.example.evooq.demo.db.jpa;

import com.example.evooq.demo.db.model.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<ActionEntity, Long> {}
