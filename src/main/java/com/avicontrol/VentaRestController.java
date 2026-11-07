package com.avicontrol;

import com.avicontrol.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaService ventaService;
    private final LoteRepository loteRepository;
    private final ClienteRepository clienteRepository;

    public VentaRestController(
            VentaService ventaService,
            LoteRepository loteRepository,
            ClienteRepository clienteRepository) {
        this.ventaService = ventaService;
        this.loteRepository = loteRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscar(@PathVariable Long id) {
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lote/{loteId}")
    public List<Venta> listarPorLote(@PathVariable Long loteId) {
        return ventaService.listarPorLote(loteId);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Map<String, Object> datos) {
        try {
            Venta venta = convertirVenta(datos);
            Venta nueva = ventaService.guardar(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo registrar la venta"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> datos) {
        try {
            Venta venta = convertirVenta(datos);
            return ResponseEntity.ok(ventaService.actualizar(id, venta));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo actualizar la venta"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            ventaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Venta convertirVenta(Map<String, Object> datos) {
        Venta venta = new Venta();

        Long loteId = Long.valueOf(datos.get("loteId").toString());

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        venta.setLote(lote);
        venta.setCantidadPollos(
                Integer.valueOf(datos.get("cantidadPollos").toString()));
        venta.setPesoTotal(new BigDecimal(datos.get("pesoTotal").toString()));
        venta.setPrecioPorKilo(
                new BigDecimal(datos.get("precioPorKilo").toString()));
        venta.setTotalVenta(
                new BigDecimal(datos.get("totalVenta").toString()));
        venta.setFecha(LocalDate.parse(datos.get("fecha").toString()));

        if (datos.get("clienteId") != null) {
            Long clienteId = Long.valueOf(datos.get("clienteId").toString());

            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            venta.setCliente(cliente);
        }

        if (datos.get("observaciones") != null) {
            venta.setObservaciones(datos.get("observaciones").toString());
        }

        return venta;
    }
}
