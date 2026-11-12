
package com.avicontrol;

import com.avicontrol.service.GastoService;

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
class GastoRestControllerTest {

    @Mock
    private GastoService gastoService;

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private GastoRestController gastoRestController;

    // PRUEBA 1: Listar gastos
    @Test
    void listarGastosExitosamente() {
        Gasto gasto1 = new Gasto();
        Gasto gasto2 = new Gasto();

        when(gastoService.listarTodos())
                .thenReturn(List.of(gasto1, gasto2));

        List<Gasto> resultado = gastoRestController.listar();

        assertEquals(2, resultado.size());
        verify(gastoService).listarTodos();
    }

    // PRUEBA 2: Buscar gasto existente
    @Test
    void buscarGastoExistente() {
        Long id = 1L;
        Gasto gasto = new Gasto();

        when(gastoService.buscarPorId(id))
                .thenReturn(Optional.of(gasto));

        ResponseEntity<Gasto> respuesta =
                gastoRestController.buscar(id);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(gasto, respuesta.getBody());
    }

    // PRUEBA 3: Buscar gasto inexistente
    @Test
    void buscarGastoInexistente() {
        when(gastoService.buscarPorId(999L))
                .thenReturn(Optional.empty());

        ResponseEntity<Gasto> respuesta =
                gastoRestController.buscar(999L);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());
    }

    // PRUEBA 4: Listar gastos por lote
    @Test
    void listarGastosPorLote() {
        Long loteId = 1L;
        Gasto gasto = new Gasto();

        when(gastoService.listarPorLote(loteId))
                .thenReturn(List.of(gasto));

        List<Gasto> resultado =
                gastoRestController.listarPorLote(loteId);

        assertEquals(1, resultado.size());
        verify(gastoService).listarPorLote(loteId);
    }

    // PRUEBA 5: Guardar gasto exitosamente
    @Test
    void guardarGastoExitosamente() {
        Lote lote = new Lote();

        when(loteRepository.findById(1L))
                .thenReturn(Optional.of(lote));

        when(gastoService.guardar(any(Gasto.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0));

        Map<String, Object> datos = Map.of(
                "loteId", 1,
                "categoria", "Alimentacion",
                "descripcion", "Compra de alimento",
                "valor", 85000,
                "fecha", "2026-11-07"
        );

        ResponseEntity<?> respuesta =
                gastoRestController.guardar(datos);

        assertEquals(HttpStatus.CREATED,
                respuesta.getStatusCode());

        assertInstanceOf(Gasto.class, respuesta.getBody());
        verify(gastoService).guardar(any(Gasto.class));
    }

    // PRUEBA 6: Guardar gasto con lote inexistente
    @Test
    void guardarGastoConLoteInexistente() {
        when(loteRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseEntity<?> respuesta =
                gastoRestController.guardar(
                        Map.of("loteId", 999));

        assertEquals(HttpStatus.BAD_REQUEST,
                respuesta.getStatusCode());

        assertEquals(
                Map.of("error", "No se pudo registrar el gasto"),
                respuesta.getBody()
        );

        verify(gastoService, never()).guardar(any());
    }

    // PRUEBA 7: Actualizar gasto exitosamente
    @Test
    void actualizarGastoExitosamente() {
        Lote lote = new Lote();

        when(loteRepository.findById(1L))
                .thenReturn(Optional.of(lote));

        when(gastoService.actualizar(eq(1L), any(Gasto.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(1));

        Map<String, Object> datos = Map.of(
                "loteId", 1,
                "categoria", "Vacunas",
                "descripcion", "Compra de vacunas",
                "valor", 25000,
                "fecha", "2026-11-07"
        );

        ResponseEntity<?> respuesta =
                gastoRestController.actualizar(1L, datos);

        assertEquals(HttpStatus.OK,
                respuesta.getStatusCode());

        assertInstanceOf(Gasto.class, respuesta.getBody());
        verify(gastoService).actualizar(eq(1L), any(Gasto.class));
    }

    // PRUEBA 8: Actualizar gasto inexistente
    @Test
    void actualizarGastoInexistente() {
        Lote lote = new Lote();

        when(loteRepository.findById(1L))
                .thenReturn(Optional.of(lote));

        when(gastoService.actualizar(eq(999L), any(Gasto.class)))
                .thenThrow(new RuntimeException("Gasto no encontrado"));

        Map<String, Object> datos = Map.of(
                "loteId", 1,
                "categoria", "Alimentacion",
                "descripcion", "Compra de alimento",
                "valor", 85000,
                "fecha", "2026-11-07"
        );

        ResponseEntity<?> respuesta =
                gastoRestController.actualizar(999L, datos);

        assertEquals(HttpStatus.BAD_REQUEST,
                respuesta.getStatusCode());

        assertEquals(
                Map.of("error", "No se pudo actualizar el gasto"),
                respuesta.getBody()
        );
    }

    // PRUEBA 9: Eliminar gasto existente
    @Test
    void eliminarGastoExitosamente() {
        ResponseEntity<Void> respuesta =
                gastoRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT,
                respuesta.getStatusCode());

        verify(gastoService).eliminar(1L);
    }

    // PRUEBA 10: Eliminar gasto inexistente
    @Test
    void eliminarGastoInexistente() {
        doThrow(new RuntimeException("Gasto no encontrado"))
                .when(gastoService).eliminar(999L);

        ResponseEntity<Void> respuesta =
                gastoRestController.eliminar(999L);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());

        verify(gastoService).eliminar(999L);
    }
}

