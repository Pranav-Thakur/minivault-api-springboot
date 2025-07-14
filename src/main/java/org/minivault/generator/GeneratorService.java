package org.minivault.generator;

import lombok.NonNull;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;

public interface GeneratorService {
    GeneratorServiceResponse generateResponse(@NonNull GeneratorServiceRequest generatorServiceRequest) throws Exception;
}
