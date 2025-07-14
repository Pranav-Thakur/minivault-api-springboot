package org.minivault.controller;

import org.minivault.payload.request.PromptGenerateRequest;
import org.minivault.payload.response.PromptGenerateResponse;
import org.minivault.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private GenerateService generateService;

    @PostMapping("/generate")
    public ResponseEntity<PromptGenerateResponse> generateFromPrompt(
            @RequestBody PromptGenerateRequest promptGenerateRequest) throws Exception {
        PromptGenerateResponse response = generateService.generateFromPrompt(promptGenerateRequest);
        return ResponseEntity.ok(response);
    }
}
