package com.devopsclassroom.dto;

import jakarta.validation.constraints.*;

public class UsuarioRequest {
    @NotBlank(message = "Login é obrigatório")
    @Size(min = 3, max = 50)
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    private String senha;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    private String nome;

    @Email(message = "Email inválido")
    private String email;

    private String telefone;

    @NotBlank(message = "Tipo de usuário é obrigatório")
    private String tipo;

    private String instituicao;

    private String bio;

    // Getters and Setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
