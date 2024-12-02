package com.ejemplo.registro.controller;

import com.ejemplo.registro.model.Search;
import com.ejemplo.registro.model.SearchRequest;
import com.ejemplo.registro.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Convertir y guardar cada SearchRequest en la base de datos
            for (SearchRequest request : searchRequests) {
                Search search = new Search();
                search.setBusqueda(request.getBusqueda());
                search.setUrl(request.getUrl());

                // Conversión de String a LocalDate
                search.setFechaBusqueda(LocalDateTime.parse(request.getFechaBusqueda(), formatter));

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

    @GetMapping("/history/{userId}")
    public ResponseEntity<Page<Search>> getHistorial(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaBusqueda").descending());
        Page<Search> historial = searchRepository.findByUserId(userId, pageable);
        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
    }


}