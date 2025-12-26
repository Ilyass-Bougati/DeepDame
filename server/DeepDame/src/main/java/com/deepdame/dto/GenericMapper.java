package com.deepdame.dto;

public interface GenericMapper<ENTITY, DTO> {
    DTO toDTO(ENTITY entity);
    ENTITY toEntity(DTO dto);
}