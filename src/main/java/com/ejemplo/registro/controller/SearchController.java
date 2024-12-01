package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.Search;
import com.ejemplo.registro.model.SearchRequest;
import com.ejemplo.registro.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchRepository searchRepository;

    public SearchController(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @PostMapping("/registerBatch")
    public ResponseEntity<?> registerSearchBatch(@RequestBody List<SearchRequest> searchRequests) {
        if (searchRequests == null || searchRequests.isEmpty()) {
            return ResponseEntity.badRequest().body("La lista de búsquedas está vacía.");
        }

        try {
            // Convertir y guardar cada SearchRequest en la base de datos
            for (SearchRequest request : searchRequests) {
                Search search = new Search();
                search.setBusqueda(request.getBusqueda());
                search.setUrl(request.getUrl());

                // Conversión de String a LocalDate
                search.setFechaBusqueda(LocalDate.parse(request.getFechaBusqueda()));

                search.setUserId(request.getUserId());
                searchRepository.save(search); // Guardar directamente con el repositorio
            }


            return ResponseEntity.ok("Búsquedas registradas con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar las búsquedas: " + e.getMessage());
        }
    }
}