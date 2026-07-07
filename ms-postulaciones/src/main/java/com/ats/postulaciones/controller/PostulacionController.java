package com.ats.postulaciones.controller;

import com.ats.postulaciones.dto.EstadoPostulacionRequest;
import com.ats.postulaciones.dto.PostulacionRequest;
import com.ats.postulaciones.dto.PostulacionResponse;
import com.ats.postulaciones.service.PostulacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postulaciones")
public class PostulacionController {

    private final PostulacionService postulacionService;

    public PostulacionController(PostulacionService postulacionService) {
        this.postulacionService = postulacionService;
    }

    @PostMapping
    public ResponseEntity<PostulacionResponse> crear(@Valid @RequestBody PostulacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postulacionService.crear(request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PostulacionResponse> actualizarEstado(@PathVariable Long id,
                                                                  @Valid @RequestBody EstadoPostulacionRequest request) {
        return ResponseEntity.ok(postulacionService.actualizarEstado(id, request));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PostulacionResponse>> historialPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(postulacionService.historialPorUsuario(idUsuario));
    }
}
