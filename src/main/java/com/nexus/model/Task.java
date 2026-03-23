package com.nexus.model;

import com.nexus.exception.NexusValidationException;

import java.time.LocalDate;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;
    private final LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;
    //novo atributo
    private int estimatedEffort;

    public Task(String title, LocalDate deadline, int estimatedEffort) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        //novo
        this.estimatedEffort = estimatedEffort;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) throws NexusValidationException {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload
        // Se falhar, incrementar totalValidationErrors e lançar NexusValidationException
        if (user == null){
            totalValidationErrors++;
            throw new NexusValidationException("Deve ser atribuído um dono para iniciar a tarefa");
        }

        if (this.status == TaskStatus.BLOCKED){
            totalValidationErrors++;
            throw new NexusValidationException("A tarefa não pode estar bloqueada para ser iniciada");
        }

        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() throws NexusValidationException {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload (decrementar)
        if (this.status == TaskStatus.BLOCKED){
            totalValidationErrors++;
            throw new NexusValidationException("Uma tarefa bloqueada não pode ser concluída");
        }

        if(this.status == TaskStatus.IN_PROGRESS){
            activeWorkload--;
        }
        this.status = TaskStatus.DONE;
    }

    public void setBlocked(boolean blocked) throws NexusValidationException {
        if (blocked) {
            if (this.status == TaskStatus.DONE){
                totalValidationErrors++;
                throw new NexusValidationException("Uma tarefa concluída não pode ser bloqueada");
            } else {
                this.status = TaskStatus.BLOCKED;
            }
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort() { return estimatedEffort; }
}