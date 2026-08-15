package com.devopsclassroom.repository;

import com.devopsclassroom.entity.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByAulaIdOrderByCriadoEmAsc(Long aulaId);
    List<Mensagem> findByAulaIdAndTipoOrderByCriadoEmAsc(Long aulaId, com.devopsclassroom.entity.TipoMensagem tipo);
}
