package com.ats.usuarios.service.impl;

import com.ats.usuarios.dto.LoginRequest;
import com.ats.usuarios.dto.LoginResponse;
import com.ats.usuarios.dto.RegistroRequest;
import com.ats.usuarios.dto.UsuarioResponse;
import com.ats.usuarios.entity.Usuario;
import com.ats.usuarios.exception.CredencialesInvalidasException;
import com.ats.usuarios.exception.EmailYaRegistradoException;
import com.ats.usuarios.exception.ResourceNotFoundException;
import com.ats.usuarios.repository.UsuarioRepository;
import com.ats.usuarios.security.JwtUtil;
import com.ats.usuarios.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        log.info("Registrando nuevo usuario con email {}", request.getEmail());
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailYaRegistradoException("Ya existe un usuario registrado con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario {} registrado con id {}", guardado.getEmail(), guardado.getId());
        return toResponse(guardado);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para {}", request.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException("Email o password incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Email o password incorrectos");
        }

        String token = jwtUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol().name());
        log.info("Login exitoso para {}", usuario.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        log.info("Consultando usuario con id {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado"));
        return toResponse(usuario);
    }

    @Override
    public List<UsuarioResponse> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }
}
