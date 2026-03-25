package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Processa arquivos de log com comandos de automacao para o ecossistema Nexus.
 */
public class LogProcessor {

    /**
     * Le e executa, linha a linha, os comandos de um arquivo de log no classpath.
     * Erros nao interrompem o processamento do lote; cada falha incrementa o
     * contador global de validacoes invalidas.
     *
     * @param fileName nome do arquivo de log no classpath
     * @param workspace workspace alvo das operacoes
     * @param users lista de usuarios cadastrados em memoria
     */
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
                                int effort = (p.length > 3) ? Integer.parseInt(p[3]) : 1; // Valor padrão de 1.
                                String projectName = (p.length > 4) ? p[4] : "";

                                Task t = new Task(nome, data, effort);
                                workspace.addTask(t);
                                if(!projectName.isEmpty()) {
                                    workspace.addTaskToProject(t.getId(), projectName);
                                }
                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> {
                                String projectName = p[1];
                                int budgetHours = Integer.parseInt(p[2]);
                                Project project = new Project(projectName, budgetHours);
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto Criado: " + projectName);
                            }
                            case "ASSIGN_USER" -> {
                                int taskId = Integer.parseInt(p[1]);
                                String userName = p[2];

                                User user = users.stream()
                                        .filter(user1 -> user1.consultUsername().equals(userName))
                                        .findFirst()
                                        .orElseThrow();


                                workspace.assignUserToTaskById(taskId, user);
                            }
                            case "CHANGE_STATUS" -> {
                                int taskId = Integer.parseInt(p[1]);
                                String status = p[2];

                                workspace.changeTaskStatus(taskId, status);
                            }
                            case "REPORT_STATUS" -> {
                                System.out.println("\n[RELATÓRIO DE STATUS] Top performers:");
                                printList(workspace.getTopPerformers());
                                System.out.println("\n[RELATÓRIO DE STATUS] Overloaded users:");
                                printList(workspace.getOverloadedUsers());
                                System.out.println("\n[RELATÓRIO DE STATUS] Most used task status:");
                                System.out.println(workspace.getMostUsedTaskStatus());
                                System.out.println("\n[RELATÓRIO DE STATUS] Saúde de cada projeto:");
                                for (Project project : workspace.getProjects()) {
                                    System.out.println("[" + project.getName() + "]: " + workspace.getProjectHealth(project) + "%");
                                }
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        incrementGlobalValidationErrors();
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    } catch (Exception e) {
                        incrementGlobalValidationErrors();
                        System.err.println("[ERRO DE EXECUÇÃO] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            incrementGlobalValidationErrors();
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }

    /**
     * Incrementa o contador global de erros de validacao do sistema.
     */
    private static void incrementGlobalValidationErrors() {
        Task.totalValidationErrors++;
    }

    /**
     * Imprime uma lista de usuarios no formato definido por {@link User#toString()}.
     *
     * @param list usuarios a serem impressos
     */
    private static void printList(List<User> list) {
        for (User item : list) {
            System.out.println(item);
        }
    }
}