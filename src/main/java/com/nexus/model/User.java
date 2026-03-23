package com.nexus.model;

import java.util.List;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) throws IllegalArgumentException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-z]{2,3}$";
        if (email == null || !email.matches(regex)){
            throw new IllegalArgumentException("Email inválido: deve seguir o formato usuario@dominio.com");
        }
        this.username = username;
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload(List<Task> allTasks) {
        long counter = 0;

        for (Task tarefa : allTasks){
            if (tarefa.getOwner()!= null && tarefa.getStatus() == TaskStatus.IN_PROGRESS && tarefa.getOwner().equals(this)){
                counter++;
            }
        }
        return counter;
    }
}