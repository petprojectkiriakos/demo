package com.example.evooq.demo.db.jpa;

import com.example.evooq.demo.db.model.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionRepository
    extends JpaRepository<ActionEntity, Long>, JpaSpecificationExecutor<ActionEntity> {}
