package org.minivault.generator.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.minivault.exception.ErrorCodes;
import org.minivault.exception.MiniVaultException;
import org.minivault.generator.GeneratorService;
import org.minivault.generator.payload.GeneratorServiceRequest;
import org.minivault.generator.payload.GeneratorServiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.FluxSink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service("ollama")
public class OllamaGeneratorService implements GeneratorService {

    @Value("${mini.vault.ollama.url}")
    private String url;

    @Value("${mini.vault.ollama.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public GeneratorServiceResponse generateResponse(@NonNull GeneratorServiceRequest generatorServiceRequest) throws Exception {
        GeneratorServiceResponse response = new GeneratorServiceResponse();
        response.setUserQueryResponse(callOllama(generatorServiceRequest.getUserQuery()));
        return response;
    }

    @Override
    public GeneratorServiceResponse generateResponseToStream(@NonNull GeneratorServiceRequest generatorServiceRequest, @NonNull FluxSink<String> emitter) throws Exception {
        try {
            GeneratorServiceResponse response = new GeneratorServiceResponse();
            response.setUserQueryResponse(callOllamaStream(generatorServiceRequest.getUserQuery(), emitter));
            return response;
        } catch (Exception e) {
            throw new MiniVaultException("Error generating reponse from ollama", ErrorCodes.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String callOllamaStream(@NonNull String userQuery, @NonNull  FluxSink<String> emitter) throws Exception {
        StringBuilder fullResponse = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        String body = String.format(
                "{\n" +
                        "  \"model\": \"%s\",\n" +
                        "  \"prompt\": \"%s\",\n" +
                        "  \"stream\": true\n" +
                        "}", model, userQuery);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    JsonNode json = mapper.readTree(line);
                    String token = json.get("response").asText();
                    fullResponse.append(token);
                    if (!emitter.isCancelled()) emitter.next(token);
                }
            }
            if (!emitter.isCancelled()) emitter.complete();
        }

        return fullResponse.toString();
    }

    private String callOllama(@NonNull String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\n" +
                        "  \"model\": \"%s\",\n" +
                        "  \"prompt\": \"%s\",\n" +
                        "  \"stream\": false\n" +
                        "}", model, prompt);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return (String) response.getBody().get("response");
    }
}
