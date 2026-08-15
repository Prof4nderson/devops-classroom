package com.devopsclassroom.dto;

public class AuthResponse {
    private String token;
    private String tipo;
    private Long userId;
    private String nome;
    private String login;
    private String tipoUsuario;

    public AuthResponse(String token, Long userId, String nome, String login, String tipoUsuario) {
        this.token = token;
        this.tipo = "Bearer";
        this.userId = userId;
        this.nome = nome;
        this.login = login;
        this.tipoUsuario = tipoUsuario;
    }

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public Long getUserId() { return userId; }
    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getTipoUsuario() { return tipoUsuario; }
}
