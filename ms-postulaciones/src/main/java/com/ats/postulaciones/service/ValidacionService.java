package com.ats.postulaciones.service;

import com.ats.postulaciones.dto.UsuarioDto;
import com.ats.postulaciones.dto.VacanteDto;

public interface ValidacionService {
    VacanteDto validarVacante(Long idVacante);
    UsuarioDto validarUsuario(Long idUsuario);
}
