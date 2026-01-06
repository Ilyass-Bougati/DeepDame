package com.deepdame.service.email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.thymeleaf.context.Context;

@Getter
@Setter
@Builder
public class EmailSendRequest {
    private String email;
    private String subject;
    private Context context;
    private String template;
}
