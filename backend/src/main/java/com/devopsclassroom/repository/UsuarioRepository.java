package com.devopsclassroom.repository;

import com.devopsclassroom.entity.TipoUsuario;
import com.devopsclassroom.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método correto usando a propriedade 'login' da entidade Usuario
    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    List<Usuario> findByTipo(TipoUsuario tipo);
}