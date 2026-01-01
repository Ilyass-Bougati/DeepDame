package com.deepdame.service.json;

import com.deepdame.exception.JsonProcessingException;

public interface JsonService<DTO> {
    String toJson(DTO dto);
    DTO parseJson(String json, Class<DTO> dtoClass) throws JsonProcessingException;
}
