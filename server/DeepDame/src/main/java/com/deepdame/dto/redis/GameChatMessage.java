package com.deepdame.dto.redis;

import java.util.UUID;

public record GameChatMessage(String sender, String content, String timestamp, UUID gameId) {}
