package org.minivault.service.impl;

import lombok.NonNull;
import org.minivault.exception.ErrorCodes;
import org.minivault.exception.MiniVaultException;
import org.minivault.generator.GeneratorService;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;
import org.minivault.payload.request.PromptGenerateRequest;
import org.minivault.payload.response.PromptGenerateResponse;
import org.minivault.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class GenerateServiceImpl implements GenerateService {

    @Value("${mini.vault.response.generator}")
    private String generatorType;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public PromptGenerateResponse generateFromPrompt(@NonNull PromptGenerateRequest promptGenerateRequest) throws Exception {
        validateRequest(promptGenerateRequest);

        GeneratorServiceResponse generatorServiceResponse = callToGenerator(promptGenerateRequest);

        PromptGenerateResponse  promptGenerateResponse = new PromptGenerateResponse();
        promptGenerateResponse.setResponse(generatorServiceResponse.getUserQueryResponse());
        return promptGenerateResponse;
    }

    private GeneratorServiceResponse callToGenerator(@NonNull PromptGenerateRequest promptGenerateRequest) throws Exception {
        GeneratorServiceRequest request = new  GeneratorServiceRequest();
        request.setUserQuery(promptGenerateRequest.getPrompt());

        GeneratorService generatorService = ctx.getBean(generatorType, GeneratorService.class);
        return generatorService.generateResponse(request);
    }

    private void validateRequest(@NonNull PromptGenerateRequest promptGenerateRequest) {
        if (promptGenerateRequest.getPrompt() == null || promptGenerateRequest.getPrompt().trim().isEmpty()) {
            throw new MiniVaultException("User prompt is not valid", ErrorCodes.INVALID_INPUT);
        }
    }
}
