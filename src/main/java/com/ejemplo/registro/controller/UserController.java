package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.FacebookUserRequest;
import com.ejemplo.registro.model.MercadoLibreUserRequest;
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

    @PostMapping("/register/mercadolibre")
    public String registerUserWithMercadoLibre(@RequestBody MercadoLibreUserRequest mercadoLibreUserRequest) {
        String authorizationCode = mercadoLibreUserRequest.getAuthorizationCode();
        System.out.println("Código de autorización recibido: " + authorizationCode);
        String clientId = "1331780205525766"; // Reemplaza con tu Client ID
        String clientSecret = "aR6XSzJy9t7OmmufkImOuPLA123exh9F"; // Reemplaza con tu Client Secret
        String redirectUri = "https://5488-187-190-206-198.ngrok-free.app"; // O tu URI de redirección

        // URL de autenticación de Mercado Libre
        String tokenUrl = "https://api.mercadolibre.com/oauth/token";

        // Crear los parámetros para obtener el token
        Map<String, String> tokenRequest = Map.of(
                "grant_type", "authorization_code",
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", authorizationCode,
                "redirect_uri", redirectUri
        );

        // Realiza la solicitud para obtener el token de acceso
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);

        if (tokenResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> tokenData = tokenResponse.getBody();
            String accessToken = (String) tokenData.get("access_token");

            // Usa el token de acceso para obtener la información del usuario
            String userInfoUrl = "https://api.mercadolibre.com/users/me?access_token=" + accessToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userData = userInfoResponse.getBody();
                String email = (String) userData.get("email");
                String name = (String) userData.get("nickname"); // Puede que el nombre se devuelva como 'nickname'

                // Verifica si el usuario ya existe por correo electrónico
                Optional<User> existingUser = userRepository.findByCorreoUser(email);
                if (existingUser.isPresent()) {
                    return "El usuario ya está registrado con Mercado Libre";
                }

                // Crea un nuevo usuario con los datos de Mercado Libre
                User user = new User();
                user.setCorreo_user(email);
                user.setNombre_user(name);
                user.setFecha_registro(new Date());
                user.setRedSocial_ID_Social(3); // Asigna '3' para registro con Mercado Libre

                userRepository.save(user);
                return "Registro con Mercado Libre exitoso";
            } else {
                return "Error al obtener la información del usuario de Mercado Libre";
            }
        } else {
            return "Error al obtener el token de Mercado Libre";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam int redSocialId
    ) {
        Optional<User> user = userRepository.findByCorreoUserAndRedSocial_ID_Social(correo, redSocialId);

        if (user.isPresent()) {
            User foundUser = user.get();

            // Validar contraseña solo si es un inicio de sesión con correo electrónico
            if (redSocialId == 1) {
                if (foundUser.getPassword_user() != null && foundUser.getPassword_user().equals(password)) {
                    return ResponseEntity.ok(foundUser);
                } else {
                    return ResponseEntity.status(401).body("Contraseña incorrecta");
                }
            } else {
                // Inicio de sesión con redes sociales no requiere validación de contraseña
                return ResponseEntity.ok(foundUser);
            }
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado o método de inicio de sesión incorrecto");
        }
    }

    @PostMapping("/login/facebook")
    public ResponseEntity<?> loginWithFacebook(@RequestBody FacebookUserRequest facebookUserRequest) {
        String accessToken = facebookUserRequest.getAccessToken();
        String facebookGraphUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(facebookGraphUrl, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userData = response.getBody();
            String email = (String) userData.get("email");

            Optional<User> user = userRepository.findByCorreoUserAndRedSocial_ID_Social(email, 2);

            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(404).body("Usuario no encontrado. Debes registrarte primero.");
            }
        } else {
            return ResponseEntity.status(401).body("Token de Facebook no válido o expirado");
        }
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleUserRequest googleUserRequest) {
        String email = googleUserRequest.getEmail();

        Optional<User> user = userRepository.findByCorreoUserAndRedSocial_ID_Social(email, 4);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado. Debes registrarte primero.");
        }
    }


    @GetMapping("/vivo")
    public String serverVivo() {
        return "Servidor Vivo";
    }
}
