package com.avicontrol;

import com.avicontrol.service.LoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LoteRestController {

    private final LoteService loteService;

    public LoteRestController(LoteService loteService) {
        this.loteService = loteService;
    }

    // Consultar todos los lotes
    @GetMapping
    public List<Lote> listar() {
        return loteService.listarTodos();
    }

    // Consultar un lote por ID
    @GetMapping("/{id}")
    public ResponseEntity<Lote> buscar(@PathVariable Long id) {
        return loteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Registrar un lote
    @PostMapping
    public ResponseEntity<Lote> guardar(@RequestBody Lote lote) {
        Lote nuevo = loteService.guardar(lote);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Actualizar un lote
    @PutMapping("/{id}")
    public ResponseEntity<Lote> actualizar(
            @PathVariable Long id,
            @RequestBody Lote lote) {
        try {
            return ResponseEntity.ok(loteService.actualizar(id, lote));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un lote
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            loteService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}