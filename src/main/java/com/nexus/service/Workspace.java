package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();

    // Getters e Adders:

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public Task getTask(int id) throws Exception {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tarefa com ID " + id + " não encontrada."));
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public Project getProject(String projectName) throws Exception {
        return projects.stream()
                .filter(project -> project.getName().equals(projectName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Projeto com nome " + projectName + " não encontrada."));
    }

    // Métodos:
    public void assignUserToTaskById(int taskId, User user) throws Exception {
        Task task = getTask(taskId);
        task.setOwner(user);
    }

    public void addTaskToProject(int taskId, String projectName) throws Exception {
        Task task = getTask(taskId);
        Project project = getProject(projectName);

        project.addTask(task);
    }
}