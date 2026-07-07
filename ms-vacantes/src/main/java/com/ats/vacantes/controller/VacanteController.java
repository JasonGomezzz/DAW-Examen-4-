package com.ats.vacantes.controller;

import com.ats.vacantes.dto.VacanteRequest;
import com.ats.vacantes.dto.VacanteResponse;
import com.ats.vacantes.service.VacanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacantes")
public class VacanteController {

    private final VacanteService vacanteService;

    public VacanteController(VacanteService vacanteService) {
        this.vacanteService = vacanteService;
    }

    @PostMapping
    public ResponseEntity<VacanteResponse> crear(@Valid @RequestBody VacanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacanteService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VacanteResponse> actualizar(@PathVariable Long id, @Valid @RequestBody VacanteRequest request) {
        return ResponseEntity.ok(vacanteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vacanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacanteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vacanteService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<VacanteResponse>> listarTodas() {
        return ResponseEntity.ok(vacanteService.listarTodas());
    }

    @GetMapping("/abiertas")
    public ResponseEntity<List<VacanteResponse>> listarAbiertas() {
        return ResponseEntity.ok(vacanteService.listarAbiertas());
    }
}
