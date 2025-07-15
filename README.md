# MiniVault API - Spring Boot

A lightweight Spring Boot REST API that simulates ModelVault-style prompt-response generation.

## 📦 Features

- `POST /api/v1/generate` receives a prompt and returns response either stubbed or local LLM generated
- `POST /api/v2/generate` receives a prompt and returns response as Stream token either stubbed or local LLM generated. To use open in browser `http://localhost:8080/stream.html`
- Logs every interaction in `logs/log.jsonl` by default logs directory will be created in project base directory.

## 🚀 Getting Started

### 1. Pull the git repo

```bash
git clone git@github.com:Pranav-Thakur/minivault-api-springboot.git
cd minivault-api-springboot
```

### 2. Set up the Project
In the `application.properties` file

mini.vault.response.generator=stub/ollama
- 'stub' for stubbed response 
- 'ollama' for generating response using local running LLM with below properties
  
  mini.vault.ollama.url=http://localhost:11434/api/generate

  mini.vault.ollama.model=gemma3:1b

  `replace url and model with values you are using in your local machine.`

  mini.vault.log.filepathwithname=filepath/filename
   
  `default used is logs/log.jsonl`, but you can use any path and filename here. We will create file at same path. While checking please to the directory You Gave.
  `logs is ignored in .gitignored`


### 3. Build the Project

```bash
brew install ollama
ollama run gemma3:1b
mvn clean package -DskipTests
mvn spring-boot:run
```

### 4. Test with cURL
```bash

curl -X POST http://localhost:8080/api/v1/generate \
     -H "Content-Type: application/json" \
     -d '{"prompt":"Hello, model!"}'
     
```
OR

`Check stream based by Openning browser with url` http://localhost:8080/stream.html

OR use curl as below

```bash

curl -N -X POST http://localhost:8080/api/v2/generate \
 -H "Content-Type: application/json" \
 -d '{"prompt":"Hello, model!"}'

```


### 5. Check 
In the project folder check `logs/log.jsonl` file or Any filepath u gave
```bash
tail logs/log.jsonl
```


## 📝 Notes on Implementation and Tradeoffs

### ✅ Implementation Highlights

- Framework: Built using Spring Boot 2.7 and Java 11 for simplicity and robust REST API support.

- Logging: Prompts and responses are logged to a newline-delimited .jsonl file at `logs/log.jsonl` for easy append-only writes and compatibility with log parsers.

- Stubbed Response: The `/api/v1/generate` endpoint returns a hardcoded echo-style response ("Echo: <prompt>") as a stand-in for model-generated text.

- LLM Response: `/api/v2/generate` for stream and `/api/v1/generate` for http response, endpoint returns model-generated text. Use browser or curl

- Dynamic Directory Handling: The application ensures the log directory (logs/) exists before attempting to write, preventing FileNotFoundException.

- To log to file `LoggingFilter` is used ==> now removed not used.

- To handle Error `ControllerAdvice` is written

- `MiniVaultException` is project level exception for better error handling with ErrorCode, httpStatus and message

- `generator` package is having the real response generating logic. Actual generator is chosen at runtime on basis of application property stated in step 2.

- As java based project so `ollama` is used local LLM. 


### Tradeoffs and Improvements

- `gemma:1b` is used due to local space availability of laptop. You can use any other model, and update the same in application.properties

- As expected no cloud LLM API's used. Streaming Output	Not implemented in stubbed version. ==> Done

- Streamed API is written but not fully functional. ==> Done

- A html based webpage to see stream based response clearly. ==> Done