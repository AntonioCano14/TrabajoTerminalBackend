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
import org.springframework.web.client.HttpClientErrorException;
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
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Verificar si ya existe un usuario con el correo y ID social 1
        Optional<User> existingUser = userRepository.findByCorreoUserAndRedSocial_ID_Social(user.getCorreo_user(), 1);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya está registrado con este correo y ID social 1");
        }

        // Si no existe, proceder con el registro
        user.setFecha_registro(new Date());
        user.setRedSocial_ID_Social(1); // Asigna '1' para correo electrónico
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registro exitoso");
    }



    @PostMapping("/register/google")
    public ResponseEntity<String> registerUserWithGoogle(@RequestBody GoogleUserRequest googleUserRequest) {
        // Verifica si el usuario ya existe por correo electrónico y RedSocial_ID_Social == 4
        Optional<User> existingUser = userRepository.findByCorreoUserAndRedSocial_ID_Social(googleUserRequest.getEmail(), 4);

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya está registrado con Google");
        }

        // Crea un nuevo usuario con los datos de Google
        User user = new User();
        user.setCorreo_user(googleUserRequest.getEmail());
        user.setNombre_user(googleUserRequest.getName());
        user.setFecha_registro(new Date());
        user.setRedSocial_ID_Social(4); // Asigna '4' para registro con Google

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registro con Google exitoso");
    }


    @PostMapping("/register/facebook")
    public ResponseEntity<String> registerUserWithFacebook(@RequestBody FacebookUserRequest facebookUserRequest) {
        String accessToken = facebookUserRequest.getAccessToken();
        String facebookGraphUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(facebookGraphUrl, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userData = response.getBody();
            String email = (String) userData.get("email");
            String name = (String) userData.get("name");

            // Verificar si el usuario ya existe por correo electrónico y red social
            Optional<User> existingUser = userRepository.findByCorreoUserAndRedSocial_ID_Social(email, 2);
            if (existingUser.isPresent()) {
                // Si el usuario ya existe, devolver un conflicto HTTP 409
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya está registrado con Facebook");
            }

            // Crear un nuevo usuario con los datos de Facebook
            User user = new User();
            user.setCorreo_user(email);
            user.setNombre_user(name);
            user.setFecha_registro(new Date());
            user.setRedSocial_ID_Social(2); // Asigna '2' para registro con Facebook

            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registro con Facebook exitoso");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token de Facebook no válido o expirado");
        }
    }



    @PostMapping("/register/mercadolibre")
    public ResponseEntity<String> registerUserWithMercadoLibre(@RequestBody MercadoLibreUserRequest mercadoLibreUserRequest) {
        String authorizationCode = mercadoLibreUserRequest.getAuthorizationCode();
        System.out.println("Código de autorización recibido: " + authorizationCode);

        String clientId = "1331780205525766"; // Reemplaza con tu Client ID
        String clientSecret = "aR6XSzJy9t7OmmufkImOuPLA123exh9F"; // Reemplaza con tu Client Secret
        String redirectUri = "https://c32c-34-56-155-250.ngrok-free.app"; // O tu URI de redirección

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

                // Verificar si el usuario ya existe por correo electrónico y red social (ID = 3 para Mercado Libre)
                Optional<User> existingUser = userRepository.findByCorreoUserAndRedSocial_ID_Social(email, 3);
                if (existingUser.isPresent()) {
                    // Si el usuario ya existe, devolver un conflicto HTTP 409
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya está registrado con Mercado Libre");
                }

                // Crear un nuevo usuario con los datos de Mercado Libre
                User user = new User();
                user.setCorreo_user(email);
                user.setNombre_user(name);
                user.setFecha_registro(new Date());
                user.setRedSocial_ID_Social(3); // Asigna '3' para registro con Mercado Libre

                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.CREATED).body("Registro con Mercado Libre exitoso");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al obtener la información del usuario de Mercado Libre");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al obtener el token de Mercado Libre");
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
                    foundUser.setUltima_sesion(new Date());
                    userRepository.save(foundUser);  // Actualiza la última sesión
                    return ResponseEntity.ok(foundUser);
                } else {
                    return ResponseEntity.status(401).body("Contraseña incorrecta");
                }
            } else {
                foundUser.setUltima_sesion(new Date());
                userRepository.save(foundUser);  // Actualiza la última sesión
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
                User foundUser = user.get();
                foundUser.setUltima_sesion(new Date());
                userRepository.save(foundUser);  // Actualiza la última sesión
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
            User foundUser = user.get();
            foundUser.setUltima_sesion(new Date());
            userRepository.save(foundUser);  // Actualiza la última sesión
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado. Debes registrarte primero.");
        }
    }

    @PostMapping("/login/mercadolibre")
    public ResponseEntity<?> loginWithMercadoLibre(@RequestBody MercadoLibreUserRequest mercadoLibreUserRequest) {
        String authorizationCode = mercadoLibreUserRequest.getAuthorizationCode();
        System.out.println("Nuevo código de autorización recibido: " + authorizationCode);

        String clientId = "1331780205525766";
        String clientSecret = "aR6XSzJy9t7OmmufkImOuPLA123exh9F";
        String redirectUri = "https://65d6-2806-2f0-9360-fb43-44ed-db-f1c5-efb2.ngrok-free.app";

        String tokenUrl = "https://api.mercadolibre.com/oauth/token";

        Map<String, String> tokenRequest = Map.of(
                "grant_type", "authorization_code",
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", authorizationCode,
                "redirect_uri", redirectUri
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);

            if (tokenResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> tokenData = tokenResponse.getBody();
                String accessToken = (String) tokenData.get("access_token");

                String userInfoUrl = "https://api.mercadolibre.com/users/me?access_token=" + accessToken;
                ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

                if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> userData = userInfoResponse.getBody();
                    String email = (String) userData.get("email");
                    String name = (String) userData.get("nickname");

                    Optional<User> user = userRepository.findByCorreoUserAndRedSocial_ID_Social(email, 3);
                    if (user.isPresent()) {
                        User foundUser = user.get();
                        foundUser.setUltima_sesion(new Date());
                        userRepository.save(foundUser);  // Actualiza la última sesión
                        Map<String, String> response = Map.of(
                                "message", "Inicio de sesión exitoso",
                                "user", user.get().getNombre_user()
                        );
                        return ResponseEntity.ok(response);
                    } else {
                        Map<String, String> response = Map.of("message", "Usuario no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Error al obtener información del usuario"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Error al obtener el token de Mercado Libre"));
            }
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor"));
        }
    }


    @GetMapping("/vivo")
    public String serverVivo() {
        return "Servidor Vivo";
    }
}
