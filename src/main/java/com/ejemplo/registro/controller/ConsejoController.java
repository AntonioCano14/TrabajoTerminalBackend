package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.Consejo;
import com.ejemplo.registro.model.HistorialAdminConsejo;
import com.ejemplo.registro.repository.ConsejoRepository;
import com.ejemplo.registro.repository.HistorialAdminConsejoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class ConsejoController {

    @Autowired
    private ConsejoRepository consejoRepository;

    // Endpoint para obtener todos los consejos
    @GetMapping("/consejos")
    public List<Consejo> obtenerConsejos() {
        return consejoRepository.findAll(); // Devuelve todos los consejos desde la base de datos
    }

    @Autowired
    private HistorialAdminConsejoRepository historialAdminConsejoRepository;

    @PutMapping("/consejos/{id}")
    public ResponseEntity<String> actualizarConsejo(
            @PathVariable int id,
            @RequestBody Consejo consejoActualizado,
            @RequestParam int adminId) {

        Optional<Consejo> consejoExistente = consejoRepository.findById(id);
        if (consejoExistente.isPresent()) {
            Consejo consejo = consejoExistente.get();

            // Actualizar los campos del consejo
            consejo.setDescripcionConsejo(consejoActualizado.getDescripcionConsejo());
            consejo.setTipoConsejo(consejoActualizado.getTipoConsejo());
            consejoRepository.save(consejo);

            // Registrar la acción en la tabla historial_admin_consejos
            HistorialAdminConsejo registro = new HistorialAdminConsejo();
            registro.setAdminId(adminId);
            registro.setConsejoId(id);
            registro.setFechaAccion(new Date());
            registro.setHora(new Time(System.currentTimeMillis()));
            registro.setAccion("Editar");
            historialAdminConsejoRepository.save(registro);

            return ResponseEntity.ok("Consejo actualizado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consejo no encontrado");
        }
    }

    @PutMapping("/consejos/{id}/delete")
    public ResponseEntity<String> eliminarConsejo(
            @PathVariable int id,
            @RequestParam int adminId) {
        Optional<Consejo> consejoExistente = consejoRepository.findById(id);
        if (consejoExistente.isPresent()) {
            Consejo consejo = consejoExistente.get();

            // Actualizar la descripción del consejo
            consejo.setDescripcionConsejo("ELIMINADO - " + consejo.getDescripcionConsejo());
            consejoRepository.save(consejo);

            // Registrar la acción en historial_admin_consejos
            HistorialAdminConsejo historial = new HistorialAdminConsejo();
            historial.setAdminId(adminId);
            historial.setConsejoId(id);
            historial.setFechaAccion(new Date());
            historial.setHora(new Time(System.currentTimeMillis()));
            historial.setAccion("Eliminar");
            historialAdminConsejoRepository.save(historial);

            return ResponseEntity.ok("Consejo eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consejo no encontrado");
        }
    }

    @PostMapping("/register/consejos")
    public ResponseEntity<String> registrarConsejo(@RequestBody Consejo consejo) {
        if (consejo.getDescripcionConsejo() == null || consejo.getDescripcionConsejo().isEmpty() ||
                consejo.getTipoConsejo() == null || consejo.getTipoConsejo().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La descripción y el tipo de consejo son obligatorios");
        }

        consejoRepository.save(consejo);
        return ResponseEntity.status(HttpStatus.CREATED).body("Consejo registrado exitosamente");
    }
}
