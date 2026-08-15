package com.devopsclassroom.dto;

import com.devopsclassroom.entity.Usuario;
import java.time.LocalDateTime;

public class UsuarioResponse {
    private Long id;
    private String login;
    private String nome;
    private String email;
    private String telefone;
    private String tipo;
    private String instituicao;
    private String avatar;
    private String bio;
    private LocalDateTime criadoEm;

    public static UsuarioResponse fromEntity(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.id = u.getId();
        r.login = u.getLogin();
        r.nome = u.getNome();
        r.email = u.getEmail();
        r.telefone = u.getTelefone();
        r.tipo = u.getTipo().name();
        r.instituicao = u.getInstituicao();
        r.avatar = u.getAvatar();
        r.bio = u.getBio();
        r.criadoEm = u.getCriadoEm();
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
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
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
