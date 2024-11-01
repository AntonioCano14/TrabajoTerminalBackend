package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.FacebookUserRequest;
import com.ejemplo.registro.model.GoogleUserRequest;
import com.ejemplo.registro.model.User;
import com.ejemplo.registro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
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

    @PostMapping("/register/facebook")
    public String registerUserWithFacebook(@RequestBody FacebookUserRequest facebookUserRequest) {
        String accessToken = facebookUserRequest.getAccessToken();
        String facebookGraphUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(facebookGraphUrl, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userData = response.getBody();
            String email = (String) userData.get("email");
            String name = (String) userData.get("name");

            // Verificar si el usuario ya existe por correo electrónico
            Optional<User> existingUser = userRepository.findByCorreoUser(email);
            if (existingUser.isPresent()) {
                return "El usuario ya está registrado con Facebook";
            }

            // Crear un nuevo usuario con los datos de Facebook
            User user = new User();
            user.setCorreo_user(email);
            user.setNombre_user(name);
            user.setFecha_registro(new Date());
            user.setRedSocial_ID_Social(2); // Asigna '2' para registro con Facebook

            userRepository.save(user);
            return "Registro con Facebook exitoso";
        } else {
            return "Token de Facebook no válido o expirado";
        }
    }

    @GetMapping("/vivo")
    public String serverVivo() {
        return "Servidor Vivo";
    }
}
