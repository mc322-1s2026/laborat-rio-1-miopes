package com.nexus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.nexus.exception.NexusValidationException;

/**
 * Representa um projeto com orcamento total em horas e tarefas vinculadas.
 */
public class Project {
    private String name;
    private List<Task> tasks = new ArrayList<>();
    private int totalBudget = 0;

    /**
     * Cria um novo projeto.
     *
     * @param name nome do projeto
     * @param totalBudget orcamento total em horas
     */
    public Project(String name, int totalBudget) {
        this.name = name;
        this.totalBudget = totalBudget;
    }

    /**
     * Adiciona uma tarefa ao projeto respeitando o limite de orcamento.
     *
     * @param t tarefa a ser adicionada
     * @throws NexusValidationException quando o orcamento for excedido
     */
    public void addTask(Task t) throws NexusValidationException {
        //Este méto do deve validar se a soma das horas de todas as tarefas atuais
        // + a nova tarefa excede o totalBudget do projeto.
        // Se exceder, lance NexusValidationException.
        int usedBudget = 0;
        for (Task t_atual : tasks) {
            usedBudget += t_atual.getEstimatedEffort();
        }

        if ((usedBudget + t.getEstimatedEffort()) > totalBudget){
            throw new NexusValidationException("O orçamento do projeto não pode ser ultrapassado");
        }

        this.tasks.add(t);
    }

    /**
     * Retorna o nome do projeto.
     *
     * @return nome do projeto
     */
    public String getName() { return name; }

    /**
     * Retorna o orcamento total do projeto em horas.
     *
     * @return orcamento em horas
     */
    public int getTotalBudget() { return totalBudget; }

    /**
     * Retorna as tarefas vinculadas em visao somente leitura.
     *
     * @return lista imutavel de tarefas
     */
    public List<Task> getTasks() { return Collections.unmodifiableList(tasks); }
}
