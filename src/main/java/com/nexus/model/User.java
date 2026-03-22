package com.nexus.model;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
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

    public long calculateWorkload() {
        return 0; 
    }
}