package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.AdminUser;
import com.ejemplo.registro.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AdminController {

    @Autowired
    private AdminUserRepository adminUserRepository;  // Usamos el repositorio para acceder a la base de datos

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminUser loginRequest) {
        // Verificamos si el nombre y la contraseña están presentes
        if (loginRequest.getNombre() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Por favor, ingresa tanto el nombre de usuario como la contraseña.");
        }

        // Buscar el admin en la base de datos por nombre
        AdminUser admin = adminUserRepository.findByNombre(loginRequest.getNombre());

        if (admin != null && admin.getPassword().equals(loginRequest.getPassword())) {
            // Si el admin existe y la contraseña es correcta, devolvemos el admin con un código OK (200)
            return ResponseEntity.status(HttpStatus.OK).body(admin);
        } else {
            // Si las credenciales no son correctas, devolvemos un error 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nombre de usuario o contraseña incorrectos.");
        }
    }
}
