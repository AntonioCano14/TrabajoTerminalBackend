package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.Consejo;
import com.ejemplo.registro.repository.ConsejoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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
}
