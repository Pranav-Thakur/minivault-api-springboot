package org.minivault.service;

import lombok.NonNull;
import org.minivault.payload.request.PromptGenerateRequest;
import org.minivault.payload.response.PromptGenerateResponse;

public interface GenerateService {
    PromptGenerateResponse generateFromPrompt(@NonNull PromptGenerateRequest promptGenerateRequest) throws Exception;
}
