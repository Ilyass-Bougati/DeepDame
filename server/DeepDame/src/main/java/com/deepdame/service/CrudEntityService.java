package com.deepdame.service;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface CrudEntityService <ENTITY, ID> {
    ENTITY findById(@NotNull ID id);
    List<ENTITY> findAll();
}
