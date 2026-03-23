package com.nexus.model;

import java.util.ArrayList;
import java.util.List;
import com.nexus.exception.NexusValidationException;

public class Project {
    private String name;
    private List<Task> tasks = new ArrayList<>();
    private int totalBudget = 0;

    public Project(String name, int totalBudget) {
        this.name = name;
        this.totalBudget = totalBudget;
    }

    public void addTask(Task t) {
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

        tasks.add(t);
    }

    public String getName() { return name; }

    public int getTotalBudget() { return totalBudget; }
}
