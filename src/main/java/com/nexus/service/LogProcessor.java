package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }
                            case "CREATE_TASK" -> {
                                // Criei variáveis para ficar mais legível e
                                // adicionei o esforço da tarefa como parametro:
                                String nome = p[1];
                                LocalDate data = LocalDate.parse(p[2]);
                                // Verifica se o argumento de effort foi passado:
                                int effort = (p.length > 3) ? Integer.parseInt(p[3]) : 0;
                                String projectName =  p[4];

                                Task t = new Task(nome, data, effort);
                                workspace.addTask(t);
                                workspace.addTaskToProject(t.getId(), projectName);
                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> {
                                String projectName =  p[1];
                                int budgetHours = Integer.parseInt(p[2]);
                                Project project = new Project(projectName, budgetHours);
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto Criado: " + projectName);
                            }
                            case "ASSIGN_USER" -> {
                                int taskId = Integer.parseInt(p[1]);
                                String userName = p[2];

                                User user = users.stream()
                                        

                                workspace.assignUserToTaskById(taskId, userName);
                            }
                            case "CHANGE_STATUS" -> {
                                int taskId = Integer.parseInt(p[1]);
                                String status = p[2];


                            }
                            case "REPORT_STATUS" -> {
                                // ¯\_(ツ)_/¯
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}