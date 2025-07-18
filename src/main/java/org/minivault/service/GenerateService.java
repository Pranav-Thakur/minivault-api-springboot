package org.minivault.service;

import lombok.NonNull;
import org.minivault.payload.request.PromptGenerateRequest;
import org.minivault.payload.response.PromptGenerateResponse;
import reactor.core.publisher.FluxSink;

public interface GenerateService {
    PromptGenerateResponse generateFromPrompt(@NonNull PromptGenerateRequest promptGenerateRequest) throws Exception;
    PromptGenerateResponse generateFromPromptToStream(@NonNull PromptGenerateRequest promptGenerateRequest, @NonNull FluxSink<String> emitter) throws Exception;
}
