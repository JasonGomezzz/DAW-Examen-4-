package com.ats.vacantes.service;

import com.ats.vacantes.dto.VacanteRequest;
import com.ats.vacantes.dto.VacanteResponse;

import java.util.List;

public interface VacanteService {
    VacanteResponse crear(VacanteRequest request);
    VacanteResponse actualizar(Long id, VacanteRequest request);
    void eliminar(Long id);
    VacanteResponse obtenerPorId(Long id);
    List<VacanteResponse> listarTodas();
    List<VacanteResponse> listarAbiertas();
}
