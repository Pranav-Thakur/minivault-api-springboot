package org.minivault.service;

import lombok.NonNull;
import org.minivault.payload.request.PromptGenerateRequest;
import org.minivault.payload.response.PromptGenerateResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface GenerateService {
    PromptGenerateResponse generateFromPrompt(@NonNull PromptGenerateRequest promptGenerateRequest) throws Exception;
    void generateFromPromptToStream(@NonNull PromptGenerateRequest promptGenerateRequest, @NonNull ResponseBodyEmitter emitter) throws Exception;
}
