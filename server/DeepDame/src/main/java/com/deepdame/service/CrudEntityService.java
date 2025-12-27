package com.deepdame.service;

import jakarta.validation.constraints.NotNull;

public interface CrudEntityService <ENTITY, ID> {
    ENTITY findById(@NotNull ID id);
}
