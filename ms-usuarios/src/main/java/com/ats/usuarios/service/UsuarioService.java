package com.ats.usuarios.service;

import com.ats.usuarios.dto.LoginRequest;
import com.ats.usuarios.dto.LoginResponse;
import com.ats.usuarios.dto.RegistroRequest;
import com.ats.usuarios.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse registrar(RegistroRequest request);
    LoginResponse login(LoginRequest request);
    UsuarioResponse obtenerPorId(Long id);
    List<UsuarioResponse> listarTodos();
}
