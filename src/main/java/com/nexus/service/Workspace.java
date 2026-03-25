package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Agrega tarefas e projetos e oferece operacoes de consulta e manutencao.
 */
public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();

    // Getters e Adders:

    /**
     * Adiciona uma tarefa ao backlog do workspace.
     *
     * @param task tarefa a ser adicionada
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Retorna uma visao imutavel das tarefas cadastradas.
     *
     * @return lista somente leitura de tarefas
     */
    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Busca uma tarefa pelo identificador unico.
     *
     * @param id identificador da tarefa
     * @return tarefa encontrada
     */
    public Task getTask(int id) throws Exception {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tarefa com ID " + id + " não encontrada."));
    }

    /**
     * Adiciona um projeto ao workspace.
     *
     * @param project projeto a ser adicionado
     */
    public void addProject(Project project) {
        projects.add(project);
    }

    /**
     * Retorna uma visao imutavel dos projetos cadastrados.
     *
     * @return lista somente leitura de projetos
     */
    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    /**
     * Busca um projeto pelo nome.
     *
     * @param projectName nome do projeto
     * @return projeto encontrado
     */
    public Project getProject(String projectName) throws Exception {
        return projects.stream()
                .filter(project -> project.getName().equals(projectName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Projeto com nome " + projectName + " não encontrada."));
    }

    // Métodos:
    /**
     * Atribui um usuario a uma tarefa existente.
     *
     * @param taskId id da tarefa
     * @param user usuario dono da tarefa
     */
    public void assignUserToTaskById(int taskId, User user) throws Exception {
        Task task = getTask(taskId);
        task.setOwner(user);
    }

    /**
     * Vincula uma tarefa existente a um projeto existente.
     *
     * @param taskId id da tarefa
     * @param projectName nome do projeto
     */
    public void addTaskToProject(int taskId, String projectName) throws Exception {
        Task task = getTask(taskId);
        Project project = getProject(projectName);

        project.addTask(task);
    }

    /**
     * Altera o status de uma tarefa existente.
     *
     * @param taskId id da tarefa
     * @param status nome do status no enum {@link TaskStatus}
     */
    public void changeTaskStatus(int taskId, String status) throws Exception {
        Task task = getTask(taskId);
        TaskStatus taskStatus = TaskStatus.valueOf(status);
        task.setStatus(taskStatus);
    }

    // Consultas:
    /*
     * Top Performers: Um método que retorna os 3 usuários que possuem o maior número de tarefas no status DONE.
     * Feito com Streams para facilitar a contagem e ordenação dos usuários com base no número de tarefas concluídas.
     */
    /**
     * Retorna os 3 usuarios com maior numero de tarefas concluidas.
     *
     * @return lista com no maximo 3 usuarios
     */
    public List<User> getTopPerformers() throws Exception {
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .map(Task::getOwner)
                .filter(Objects::nonNull) // Filtra tarefas sem dono
                // Transforma em um mapa (User -> contagem de tarefas concluídas) e depois ordena por contagem decrescente:
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Ordena por contagem decrescente
                .limit(3) // Limita aos top 3
                .map(Map.Entry::getKey)
                .toList();
    }

    /*
    * Overloaded Users: Listar todos os usuários cuja carga de trabalho atual (IN_PROGRESS) ultrapassa 10 tarefas.
     * */
    /**
     * Retorna usuarios com carga atual superior a 10 tarefas em andamento.
     *
     * @return usuarios sobrecarregados
     */
    public List<User> getOverloadedUsers() throws Exception {
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .map(Task::getOwner)
                .filter(Objects::nonNull) // Filtra tarefas sem dono
                // Transforma em um mapa (User -> contagem de tarefas em andamento) e depois filtra por contagem > 10:
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 10) // Filtra usuários com mais de 10 tarefas em andamento
                .map(Map.Entry::getKey)
                .toList();
    }

    /*
     * Project Health: Para um dado projeto, calcular o percentual de conclusão (Tarefas DONE / Total de Tarefas).
     * */
    /**
     * Calcula o percentual de tarefas concluidas de um projeto.
     *
     * @param project projeto a ser analisado
     * @return percentual de conclusao entre 0 e 100
     */
    public float getProjectHealth(Project project) throws Exception {
        int totalTarefas = project.getTasks().size();
        if (totalTarefas == 0) return 100.0f; // Se não houver tarefas, consideramos o projeto como 100% concluído (ou poderia ser 0%, dependendo da interpretação)

        int tarefasFeitas = project.getTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .toList()
                .size();

        return (float) tarefasFeitas / totalTarefas * 100;
    }

    /*
     * Global Bottlenecks: Identificar qual o status que possui o maior número de tarefas no sistema (exceto DONE).
     * */
    /**
     * Identifica o status mais frequente no sistema, desconsiderando DONE.
     *
     * @return status com maior ocorrencia
     */
    public TaskStatus getMostUsedTaskStatus() throws Exception {
        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE) // Exclui tarefas concluídas
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue()) // Encontra o status com a maior contagem
                .orElseThrow(() -> new IllegalStateException("Não há tarefas para analisar"))
                .getKey();
    }
}