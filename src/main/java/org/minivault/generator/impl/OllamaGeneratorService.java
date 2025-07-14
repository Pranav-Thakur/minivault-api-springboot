package org.minivault.generator.impl;

import lombok.NonNull;
import org.minivault.generator.GeneratorService;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service("ollama")
public class OllamaGeneratorService implements GeneratorService {

    @Value("${mini.vault.ollama.url}")
    private String url;

    @Value("${mini.vault.ollama.model}")
    private String model;

    @Value("${mini.vault.ollama.response.stream}")
    private boolean stream;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public GeneratorServiceResponse generateResponse(@NonNull GeneratorServiceRequest generatorServiceRequest) throws Exception {
        GeneratorServiceResponse response = new GeneratorServiceResponse();
        response.setUserQueryResponse(callOllama(generatorServiceRequest.getUserQuery()));
        return response;
    }

    private String callOllama(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\n" +
                        "  \"model\": \"%s\",\n" +
                        "  \"prompt\": \"%s\",\n" +
                        "  \"stream\": %b\n" +
                        "}", model, prompt, stream);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return (String) response.getBody().get("response");
    }
}
