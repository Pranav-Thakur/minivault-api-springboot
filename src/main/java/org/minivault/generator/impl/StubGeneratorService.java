package org.minivault.generator.impl;

import lombok.NonNull;
import org.minivault.generator.GeneratorService;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Service("stub")
public class StubGeneratorService implements GeneratorService {

    @Override
    public GeneratorServiceResponse generateResponse(@NonNull GeneratorServiceRequest generatorServiceRequest) throws Exception {
        String prompt = generatorServiceRequest.getUserQuery();
        String stubResponse = "Echo: " + prompt;

        GeneratorServiceResponse response = new GeneratorServiceResponse();
        response.setUserQueryResponse(stubResponse);
        return response;
    }

    @Override
    public void generateResponseToStream(@NonNull GeneratorServiceRequest generatorServiceRequest, @NonNull ResponseBodyEmitter emitter) throws Exception {
        String prompt = generatorServiceRequest.getUserQuery();
        String stubResponse = "Echo: " + prompt;
        emitter.send(stubResponse);
        emitter.complete();
    }
}
