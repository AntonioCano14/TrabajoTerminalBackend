package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.GoogleUserRequest;
import com.ejemplo.registro.model.User;
import com.ejemplo.registro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        user.setFecha_registro(new Date());
        user.setRedSocial_ID_Social(1); // Asigna '1' para correo electrónico
        userRepository.save(user);
        return "Registro exitoso";
    }

    @PostMapping("/register/google")
    public String registerUserWithGoogle(@RequestBody GoogleUserRequest googleUserRequest) {
        // Verifica si el usuario ya existe por correo electrónico
        Optional<User> existingUser = userRepository.findByCorreoUser(googleUserRequest.getEmail());

        if (existingUser.isPresent()) {
            return "El usuario ya está registrado con Google";
        }

        // Crea un nuevo usuario con los datos de Google
        User user = new User();
        user.setCorreo_user(googleUserRequest.getEmail());
        user.setNombre_user(googleUserRequest.getName());
        user.setFecha_registro(new Date());
        user.setRedSocial_ID_Social(4); // Asigna '4' para registro con Google

        userRepository.save(user);
        return "Registro con Google exitoso";
    }

    @GetMapping("/vivo")
    public String serverVivo() {
        return "Servidor Vivo";
    }
}
