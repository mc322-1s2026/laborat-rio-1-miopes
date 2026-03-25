package com.nexus.exception;

/**
 * Excecao para violacoes de regras de negocio do dominio Nexus.
 */
public class NexusValidationException extends RuntimeException {
    /**
     * Cria uma excecao de validacao com mensagem descritiva.
     *
     * @param message descricao da violacao detectada
     */
    public NexusValidationException(String message) {
        super(message);
        // Dica para o aluno: Incrementar contador global de erros aqui? 
        // Ou melhor deixar para a Task gerenciar.
    }
}