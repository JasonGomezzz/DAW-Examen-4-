package com.ats.postulaciones.repository;

import com.ats.postulaciones.entity.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {
    List<Postulacion> findByIdUsuario(Long idUsuario);
}
