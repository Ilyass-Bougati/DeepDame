package com.deepdame.service.json;

import com.deepdame.exception.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class JsonServiceImpl<DTO> implements JsonService<DTO> {
    private final ObjectMapper objectMapper;

    @Override
    public String toJson(DTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON error", e);
        }
    }

    @Override
    public DTO parseJson(String json, Class<DTO> dtoClass) throws JsonProcessingException {
        return objectMapper.readValue(json, dtoClass);
    }
}
