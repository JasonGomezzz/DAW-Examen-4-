package com.ats.vacantes.repository;

import com.ats.vacantes.entity.EstadoVacante;
import com.ats.vacantes.entity.Vacante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacanteRepository extends JpaRepository<Vacante, Long> {
    List<Vacante> findByEstado(EstadoVacante estado);
}
