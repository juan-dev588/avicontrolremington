package com.avicontrol;

import com.avicontrol.service.GastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gastos")
public class GastoRestController {

    private final GastoService gastoService;
    private final LoteRepository loteRepository;

    public GastoRestController(
            GastoService gastoService,
            LoteRepository loteRepository) {
        this.gastoService = gastoService;
        this.loteRepository = loteRepository;
    }

    @GetMapping
    public List<Gasto> listar() {
        return gastoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> buscar(@PathVariable Long id) {
        return gastoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lote/{loteId}")
    public List<Gasto> listarPorLote(@PathVariable Long loteId) {
        return gastoService.listarPorLote(loteId);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Map<String, Object> datos) {
        try {
            Gasto gasto = convertirGasto(datos);
            Gasto nuevo = gastoService.guardar(gasto);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo registrar el gasto"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> datos) {
        try {
            Gasto gasto = convertirGasto(datos);

            return ResponseEntity.ok(
                    gastoService.actualizar(id, gasto));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo actualizar el gasto"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            gastoService.eliminar(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Gasto convertirGasto(Map<String, Object> datos) {

        Gasto gasto = new Gasto();

        Long loteId = Long.valueOf(datos.get("loteId").toString());

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() ->
                        new RuntimeException("Lote no encontrado"));

        gasto.setLote(lote);
        gasto.setCategoria(datos.get("categoria").toString());
        gasto.setDescripcion(datos.get("descripcion").toString());
        gasto.setValor(new BigDecimal(datos.get("valor").toString()));
        gasto.setFecha(LocalDate.parse(datos.get("fecha").toString()));

        if (datos.get("observaciones") != null) {
            gasto.setObservaciones(
                    datos.get("observaciones").toString());
        }

        return gasto;
    }
}