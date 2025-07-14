# MiniVault API - Spring Boot

A lightweight Spring Boot REST API that simulates ModelVault-style prompt-response generation.

## 📦 Features

- `POST /generate` receives a prompt and returns response
- Logs every interaction in `logs/log.jsonl`
- Easy to extend with local model logic

## 🚀 Getting Started

### 1. Build the Project

```bash
mvn clean install