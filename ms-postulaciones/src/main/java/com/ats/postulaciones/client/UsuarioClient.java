package com.ats.postulaciones.client;

import com.ats.postulaciones.dto.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDto obtenerPorId(@PathVariable("id") Long id);
}
