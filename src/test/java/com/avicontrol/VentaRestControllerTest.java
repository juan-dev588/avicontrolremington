

package com.avicontrol;

import com.avicontrol.service.VentaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaRestControllerTest {

    @Mock
    private VentaService ventaService;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private VentaRestController ventaRestController;

    // PRUEBA 1: Listar ventas correctamente
    @Test
    void listarVentasExitosamente() {
        Venta venta1 = new Venta();
        Venta venta2 = new Venta();

        when(ventaService.listarTodos())
                .thenReturn(List.of(venta1, venta2));

        List<Venta> resultado = ventaRestController.listar();

        assertEquals(2, resultado.size());
        verify(ventaService).listarTodos();
    }

    // PRUEBA 2: Buscar una venta existente
    @Test
    void buscarVentaExistente() {
        Long id = 1L;
        Venta venta = new Venta();

        when(ventaService.buscarPorId(id))
                .thenReturn(Optional.of(venta));

        ResponseEntity<Venta> respuesta =
                ventaRestController.buscar(id);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(venta, respuesta.getBody());
    }

    // PRUEBA 3: Buscar una venta inexistente
    @Test
    void buscarVentaInexistente() {
        Long id = 999L;

        when(ventaService.buscarPorId(id))
                .thenReturn(Optional.empty());

        ResponseEntity<Venta> respuesta =
                ventaRestController.buscar(id);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());
    }

    // PRUEBA 4: Listar ventas por lote
    @Test
    void listarVentasPorLote() {
        Long loteId = 1L;
        Venta venta = new Venta();

        when(ventaService.listarPorLote(loteId))
                .thenReturn(List.of(venta));

        List<Venta> resultado =
                ventaRestController.listarPorLote(loteId);

        assertEquals(1, resultado.size());
        verify(ventaService).listarPorLote(loteId);
    }

    // PRUEBA 5: Registrar una venta con lote inexistente
    @Test
    void guardarVentaConLoteInexistente() {
        Map<String, Object> datos = Map.of(
                "loteId", 999
        );

        when(loteRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseEntity<?> respuesta =
                ventaRestController.guardar(datos);

        assertEquals(HttpStatus.BAD_REQUEST,
                respuesta.getStatusCode());

        assertEquals(
                Map.of("error", "No se pudo registrar la venta"),
                respuesta.getBody()
        );

        verify(ventaService, never()).guardar(any());
    }

    // PRUEBA 6: Eliminar una venta existente
    @Test
    void eliminarVentaExitosamente() {
        Long id = 1L;

        ResponseEntity<Void> respuesta =
                ventaRestController.eliminar(id);

        assertEquals(HttpStatus.NO_CONTENT,
                respuesta.getStatusCode());

        verify(ventaService).eliminar(id);
    }

    // PRUEBA 7: Eliminar una venta inexistente
    @Test
    void eliminarVentaInexistente() {
        Long id = 999L;

        doThrow(new RuntimeException("Venta no encontrada"))
                .when(ventaService).eliminar(id);

        ResponseEntity<Void> respuesta =
                ventaRestController.eliminar(id);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());

        verify(ventaService).eliminar(id);
    }
}
