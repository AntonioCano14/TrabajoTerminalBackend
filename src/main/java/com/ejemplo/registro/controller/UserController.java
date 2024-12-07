package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.*;
import com.ejemplo.registro.repository.AdminHasUserRepository;
import com.ejemplo.registro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.util.Date;
import java.util.List;
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
    public ResponseEntity<User> loginWithMercadoLibre(@RequestBody MercadoLibreUserRequest mercadoLibreUserRequest) {
        String authorizationCode = mercadoLibreUserRequest.getAuthorizationCode();
        System.out.println("Nuevo código de autorización recibido: " + authorizationCode);

        String clientId = "1331780205525766";
        String clientSecret = "aR6XSzJy9t7OmmufkImOuPLA123exh9F";
        String redirectUri = "https://c32c-34-56-155-250.ngrok-free.app";

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

                    System.out.println("Correo obtenido de Mercado Libre: " + email);
                    System.out.println("Nombre obtenido de Mercado Libre: " + name);

                    Optional<User> user = userRepository.findByCorreoUserAndRedSocial_ID_Social(email, 3);
                    if (user.isPresent()) {
                        User foundUser = user.get();
                        foundUser.setUltima_sesion(new Date());
                        userRepository.save(foundUser);  // Actualiza la última sesión
                        return ResponseEntity.ok(foundUser); // Devuelve el objeto User directamente
                    } else {
                        System.out.println("El usuario con correo " + email + " no fue encontrado.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users")
    public List<User> obtenerUsuarios() {
        return userRepository.findAll(); // Devuelve todos los usuarios desde la base de datos
    }

    @Autowired
    private AdminHasUserRepository adminHasUserRepository;

    @PutMapping("/users/{id}")
    public ResponseEntity<String> actualizarUsuario(
            @PathVariable int id,
            @RequestBody User userActualizado,
            @RequestParam int adminId) {

        Optional<User> usuarioExistente = userRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            User usuario = usuarioExistente.get();

            // Actualizar los campos del usuario
            usuario.setNombre_user(userActualizado.getNombre_user());
            usuario.setPassword_user(userActualizado.getPassword_user());
            usuario.setCorreo_user(userActualizado.getCorreo_user());
            userRepository.save(usuario);

            // Registrar la acción en la tabla admin_has_user
            AdminHasUser registro = new AdminHasUser();
            AdminHasUserId registroId = new AdminHasUserId();
            registroId.setAdmin_ID_admin(adminId);
            registroId.setUser_ID_user(id);
            registroId.setFecha_accion(new Date());
            registroId.setHora(new Time(System.currentTimeMillis()));

            registro.setId(registroId);
            registro.setAccion("Editar");

            // Guardar el registro en la base de datos
            adminHasUserRepository.save(registro);

            return ResponseEntity.ok("Usuario actualizado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @PutMapping("/users/{id}/delete")
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable int id,
            @RequestParam int adminId) {

        Optional<User> usuarioExistente = userRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            User usuario = usuarioExistente.get();

            // Conservar solo los datos necesarios y marcar como eliminado
            usuario.setNombre_user(usuario.getNombre_user() + " (ELIMINADO)");
            usuario.setCorreo_user(usuario.getCorreo_user() + " (ELIMINADO)");
            usuario.setPassword_user(null); // Eliminar la contraseña
            usuario.setFecha_registro(null); // Limpiar fecha de registro
            usuario.setUltima_sesion(null); // Limpiar última sesión
            userRepository.save(usuario);

            // Registrar la acción en la tabla admin_has_user
            AdminHasUser registro = new AdminHasUser();
            AdminHasUserId registroId = new AdminHasUserId();
            registroId.setAdmin_ID_admin(adminId);
            registroId.setUser_ID_user(id);
            registroId.setFecha_accion(new Date());
            registroId.setHora(new Time(System.currentTimeMillis()));

            registro.setId(registroId);
            registro.setAccion("Eliminar");
            adminHasUserRepository.save(registro);

            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }



    @GetMapping("/vivo")
    public String serverVivo() {
        return "Servidor Vivo";
    }
}
