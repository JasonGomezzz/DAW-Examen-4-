package com.ats.postulaciones.client;

import com.ats.postulaciones.dto.VacanteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-vacantes")
public interface VacanteClient {

    @GetMapping("/api/vacantes/{id}")
    VacanteDto obtenerPorId(@PathVariable("id") Long id);
}
