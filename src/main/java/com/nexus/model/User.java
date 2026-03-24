package com.nexus.model;

import java.util.List;

/**
 * Representa um usuario do sistema com nome e email validados.
 */
public class User {
    private final String username;
    private final String email;

    /**
     * Cria um novo usuario.
     *
     * @param username identificador textual do usuario
     * @param email email valido do usuario
     */
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

    /**
     * Consulta o email do usuario.
     *
     * @return email cadastrado
     */
    public String consultEmail() {
        return email;
    }

    /**
     * Consulta o nome de usuario.
     *
     * @return username cadastrado
     */
    public String consultUsername() {
        return username;
    }

    /**
     * Conta tarefas em andamento atribuidas ao usuario.
     *
     * @param allTasks todas as tarefas conhecidas no sistema
     * @return quantidade de tarefas IN_PROGRESS pertencentes ao usuario
     */
    public long calculateWorkload(List<Task> allTasks) {
        long counter = 0;

        for (Task tarefa : allTasks){
            if (tarefa.getOwner()!= null && tarefa.getStatus() == TaskStatus.IN_PROGRESS && tarefa.getOwner().equals(this)){
                counter++;
            }
        }
        return counter;
    }
    /**
     * Gera representacao textual do usuario.
     *
     * @return texto formatado com username e email
     */
    @Override
    public String toString() {
        return "User {\n" +
                "\tusername = '" + username + '\'' +
                "\n\temail    = '" + email + '\'' +
                "\n}\n";
    }
}