package com.tasktracker.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tasktracker.cli.dto.ProjectDTO;
import com.tasktracker.cli.dto.TaskDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class ApiClient {

    private static final String API_BASE = "http://localhost:8080";
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public List<ProjectDTO> fetchProjects() {
        String json = get("/public/Project/read");

        try {
            ProjectDTO[] projects = mapper.readValue(json, ProjectDTO[].class);
            return Arrays.asList(projects);
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao parsear projetos: " + e.getMessage());
            return List.of();
        }
    }

    public List<TaskDTO> fetchTasks(Long projectId) {
        String json = get("/admin/" + projectId + "/task/read");

        try {
            TaskDTO[] tasks = mapper.readValue(json, TaskDTO[].class);
            return Arrays.asList(tasks);
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao parsear tasks: " + e.getMessage());
            return List.of();
        }
    }

    private String get(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + path))
                    .timeout(Duration.ofSeconds(5))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                System.err.println("[ERRO] API retornou status: " + response.statusCode());
                return "[]";
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[ERRO] API indisponível em " + API_BASE + " — verifique se o servidor está rodando.");
            return "[]";
        } catch (Exception e) {
            System.err.println("[ERRO] Falha na requisição HTTP: " + e.getMessage());
            return "[]";
        }
    }
}
