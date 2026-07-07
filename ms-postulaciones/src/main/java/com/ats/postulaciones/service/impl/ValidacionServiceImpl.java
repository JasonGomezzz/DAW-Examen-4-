package com.ats.postulaciones.service.impl;

import com.ats.postulaciones.client.UsuarioClient;
import com.ats.postulaciones.client.VacanteClient;
import com.ats.postulaciones.dto.UsuarioDto;
import com.ats.postulaciones.dto.VacanteDto;
import com.ats.postulaciones.exception.ResourceNotFoundException;
import com.ats.postulaciones.exception.ServicioNoDisponibleException;
import com.ats.postulaciones.service.ValidacionService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidacionServiceImpl implements ValidacionService {

    private final UsuarioClient usuarioClient;
    private final VacanteClient vacanteClient;

    @Value("${app.mensaje-error-servicio}")
    private String mensajeErrorServicio;

    public ValidacionServiceImpl(UsuarioClient usuarioClient, VacanteClient vacanteClient) {
        this.usuarioClient = usuarioClient;
        this.vacanteClient = vacanteClient;
    }

    @Override
    @Retry(name = "vacanteService", fallbackMethod = "vacanteFallback")
    @CircuitBreaker(name = "vacanteService", fallbackMethod = "vacanteFallback")
    public VacanteDto validarVacante(Long idVacante) {
        log.info("Consultando ms-vacantes para validar vacante {}", idVacante);
        return vacanteClient.obtenerPorId(idVacante);
    }

    @Override
    @Retry(name = "usuarioService", fallbackMethod = "usuarioFallback")
    @CircuitBreaker(name = "usuarioService", fallbackMethod = "usuarioFallback")
    public UsuarioDto validarUsuario(Long idUsuario) {
        log.info("Consultando ms-usuarios para validar usuario {}", idUsuario);
        return usuarioClient.obtenerPorId(idUsuario);
    }

    private VacanteDto vacanteFallback(Long idVacante, Throwable throwable) {
        if (throwable instanceof FeignException.NotFound) {
            throw new ResourceNotFoundException("La vacante con id " + idVacante + " no existe");
        }
        log.error("Fallback activado consultando vacante {}: {}", idVacante, throwable.getMessage());
        throw new ServicioNoDisponibleException(mensajeErrorServicio);
    }

    private UsuarioDto usuarioFallback(Long idUsuario, Throwable throwable) {
        if (throwable instanceof FeignException.NotFound) {
            throw new ResourceNotFoundException("El usuario con id " + idUsuario + " no existe");
        }
        log.error("Fallback activado consultando usuario {}: {}", idUsuario, throwable.getMessage());
        throw new ServicioNoDisponibleException(mensajeErrorServicio);
    }
}
