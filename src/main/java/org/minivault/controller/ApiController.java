package org.minivault.controller;

import org.minivault.payload.request.PromptGenerateRequest;
import org.minivault.payload.response.PromptGenerateResponse;
import org.minivault.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private GenerateService generateService;

    @PostMapping("/v1/generate")
    public ResponseEntity<PromptGenerateResponse> generateFromPrompt(
            @RequestBody PromptGenerateRequest promptGenerateRequest) throws Exception {
        PromptGenerateResponse response = generateService.generateFromPrompt(promptGenerateRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/v2/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter generateStreamFromPrompt(
            @RequestBody PromptGenerateRequest promptGenerateRequest) throws Exception {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                generateService.generateFromPromptToStream(promptGenerateRequest, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
