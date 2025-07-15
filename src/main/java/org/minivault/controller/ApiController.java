package org.minivault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.minivault.payload.request.PromptGenerateRequest;
import org.minivault.payload.response.PromptGenerateResponse;
import org.minivault.service.GenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${mini.vault.log.filepathwithname}")
    private String logFilePathWithFileName;

    @Autowired
    private GenerateService generateService;

    @PostMapping("/v1/generate")
    public ResponseEntity<PromptGenerateResponse> generateFromPrompt(
            @RequestBody PromptGenerateRequest promptGenerateRequest) throws Exception {
        return ResponseEntity.ok(generateResponse(promptGenerateRequest, null));
    }


    @PostMapping(value = "/v2/generate", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> generateStreamFromPrompt(
            @RequestBody PromptGenerateRequest promptGenerateRequest) throws Exception {
        return Flux.create(emitter -> {

            new Thread(() -> {
                generateResponse(promptGenerateRequest, emitter);
            }).start();

            //if (!emitter.isCancelled()) emitter.next("Wait while processing !");
        });
    }

    private PromptGenerateResponse generateResponse(@NonNull PromptGenerateRequest promptGenerateRequest, FluxSink<String> emitter) {
        PromptGenerateResponse response = null;
        try {
            long start = System.currentTimeMillis();
            if (emitter != null)
                response = generateService.generateFromPromptToStream(promptGenerateRequest, emitter);
            else
                response =  generateService.generateFromPrompt(promptGenerateRequest);

            long duration = System.currentTimeMillis() - start;

            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("timestamp", start);
            logData.put("durationMs", duration);
            logData.put("request", promptGenerateRequest.getPrompt());
            logData.put("response", response.getResponse());

            logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(logData));
            logDataToFile(logData);
        } catch (Exception e) {
            if (!emitter.isCancelled()) emitter.next(e.getMessage());
        }

        return response;
    }

    private void logDataToFile(@NonNull Map<String, Object> logData) throws IOException {
        File logDir = new File(getDirFromFilePathWithName(logFilePathWithFileName));
        if (!logDir.exists()) {
            logDir.mkdirs(); // Creates directory if it doesn't exist
        }

        try (FileWriter fw = new FileWriter(logFilePathWithFileName, true)) {
            fw.write(mapper.writeValueAsString(logData) + "\n");
        }
    }

    private String getDirFromFilePathWithName(@NonNull String logFilePathWithFileName) {
        Path path = Paths.get(logFilePathWithFileName);
        String dir = path.getParent() != null ? path.getParent().toString() : ".";
        return dir;
    }
}
