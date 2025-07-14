package org.minivault.generator;

import lombok.NonNull;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface GeneratorService {
    GeneratorServiceResponse generateResponse(@NonNull GeneratorServiceRequest generatorServiceRequest) throws Exception;
    void generateResponseToStream(@NonNull GeneratorServiceRequest generatorServiceRequest, @NonNull ResponseBodyEmitter emitter) throws Exception;
}
