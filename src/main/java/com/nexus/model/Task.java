package com.nexus.model;

import com.nexus.exception.NexusValidationException;

import java.time.LocalDate;

/**
 * Representa uma tarefa com ciclo de vida controlado por regras de negocio.
 */
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
    private int estimatedEffort;

    /**
     * Cria uma nova tarefa.
     *
     * @param title titulo da tarefa
     * @param deadline data limite de entrega
     * @param estimatedEffort esforco estimado em horas
     */
    public Task(String title, LocalDate deadline, int estimatedEffort) throws Exception {
        this.id = nextId++;

        if (deadline == null) {
            totalValidationErrors++;
            throw new IllegalArgumentException("A data de deadline não pode ser nula");
        }
        this.deadline = deadline;

        setTitle(title);
        this.status = TaskStatus.TO_DO;
        setEstimatedEffort(estimatedEffort);

        totalTasksCreated++;
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress() throws NexusValidationException {
        // Lógica de proteção e atualizar activeWorkload
        // Se falhar, incrementar totalValidationErrors e lançar NexusValidationException
        if (owner == null) {
            totalValidationErrors++;
            throw new NexusValidationException("Deve ser atribuído um dono para iniciar a tarefa");
        }

        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("A tarefa não pode estar bloqueada para ser iniciada");
        }

        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() throws NexusValidationException {
        // Lógica de proteção e atualizar activeWorkload (decrementar)
        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Uma tarefa bloqueada não pode ser concluída");
        }

        if (this.status == TaskStatus.IN_PROGRESS) {
            activeWorkload--;
        }
        this.status = TaskStatus.DONE;
    }

    private void setBlocked() throws NexusValidationException {
        if (this.status == TaskStatus.DONE) {
            totalValidationErrors++;
            throw new NexusValidationException("Uma tarefa concluída não pode ser bloqueada");
        } else {
            this.status = TaskStatus.BLOCKED;
        }
    }

    // Getters:
    /**
     * Retorna o identificador unico da tarefa.
     *
     * @return id da tarefa
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o status atual da tarefa.
     *
     * @return status atual
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Retorna o titulo da tarefa.
     *
     * @return titulo atual
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retorna o deadline da tarefa.
     *
     * @return data limite
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Retorna o usuario dono da tarefa.
     *
     * @return owner atual, podendo ser nulo
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Retorna o esforco estimado da tarefa em horas.
     *
     * @return esforco estimado
     */
    public int getEstimatedEffort() {
        return estimatedEffort;
    }

    // Setters:
    /**
     * Atualiza o titulo da tarefa.
     *
     * @param title novo titulo
     */
    public void setTitle(String title) throws Exception {
        if (title == null || title.isBlank()) {
            totalValidationErrors++;
            throw new NexusValidationException("O título da tarefa não pode ser vazio");
        }

        this.title = title;
    }

    /**
     * Define o owner da tarefa.
     *
     * @param user usuario responsavel
     */
    public void setOwner(User user) {
        this.owner = user;
    }

    private void setEstimatedEffort(int estimatedEffort) throws NexusValidationException {
        if (estimatedEffort <= 0) {
            totalValidationErrors++;
            throw new NexusValidationException("O esforço estimado deve ser maior que zero");
        }
        this.estimatedEffort = estimatedEffort;
    }

    /**
     * Solicita transicao de status respeitando as regras da maquina de estados.
     *
     * @param status status desejado
     */
    public void setStatus(TaskStatus status) throws NexusValidationException {
        if (status == null) {
            totalValidationErrors++;
            throw new NexusValidationException("O status da tarefa não pode ser vazio");
        }
        if (status == TaskStatus.BLOCKED) {
            setBlocked();
        } else if (status == TaskStatus.IN_PROGRESS) {
            moveToInProgress();
        } else if (status == TaskStatus.DONE) {
            markAsDone();
        }
    }
}