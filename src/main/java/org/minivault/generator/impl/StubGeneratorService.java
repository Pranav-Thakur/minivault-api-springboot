package org.minivault.generator.impl;

import lombok.NonNull;
import org.minivault.generator.GeneratorService;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

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
    public GeneratorServiceResponse generateResponseToStream(@NonNull GeneratorServiceRequest generatorServiceRequest, @NonNull FluxSink<String> emitter) throws Exception {
        String prompt = generatorServiceRequest.getUserQuery();
        String stubResponse = "Echo: " + prompt;
        if (!emitter.isCancelled()) emitter.next(stubResponse);

        GeneratorServiceResponse response = new GeneratorServiceResponse();
        response.setUserQueryResponse(stubResponse);
        return response;
    }
}
