package org.minivault.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${mini.vault.log.filepathwithname}")
    private String logFilePathWithFileName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        long start = System.currentTimeMillis();
        chain.doFilter(wrappedRequest, wrappedResponse);
        long duration = System.currentTimeMillis() - start;

        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("durationMs", duration);
        logData.put("request", parseJsonOrReturnString(getRequestBody(wrappedRequest)));
        logData.put("response", parseJsonOrReturnString(getResponseBody(wrappedResponse)));
        logData.put("status", wrappedResponse.getStatus());

        logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(logData));
        logDataToFile(logData);

        wrappedResponse.copyBodyToResponse();
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

    private Map<String, String> getHeadersMap(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        return buf.length > 0 ? new String(buf, StandardCharsets.UTF_8) : "";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        return buf.length > 0 ? new String(buf, StandardCharsets.UTF_8) : "";
    }

    private Object parseJsonOrReturnString(String body) {
        if (body == null || body.trim().isEmpty()) return "[empty]";
        try {
            return mapper.readValue(body, Object.class);
        } catch (JsonProcessingException e) {
            return body; // fallback to raw string
        }
    }
}
