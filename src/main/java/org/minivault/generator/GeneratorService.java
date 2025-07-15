package org.minivault.generator;

import lombok.NonNull;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;
import reactor.core.publisher.FluxSink;

public interface GeneratorService {
    GeneratorServiceResponse generateResponse(@NonNull GeneratorServiceRequest generatorServiceRequest) throws Exception;
    GeneratorServiceResponse generateResponseToStream(@NonNull GeneratorServiceRequest generatorServiceRequest, @NonNull FluxSink<String> emitter) throws Exception;
}
