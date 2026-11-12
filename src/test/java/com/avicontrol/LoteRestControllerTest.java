
package com.avicontrol;

import com.avicontrol.service.LoteService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteRestControllerTest {

    @Mock
    private LoteService loteService;

    @InjectMocks
    private LoteRestController loteRestController;

    // PRUEBA 1: Listar lotes exitosamente
    @Test
    void listarLotesExitosamente() {
        Lote lote1 = new Lote();
        Lote lote2 = new Lote();

        when(loteService.listarTodos())
                .thenReturn(List.of(lote1, lote2));

        List<Lote> resultado = loteRestController.listar();

        assertEquals(2, resultado.size());
        verify(loteService).listarTodos();
    }

    // PRUEBA 2: Buscar un lote existente
    @Test
    void buscarLoteExistente() {
        Long id = 1L;
        Lote lote = new Lote();

        when(loteService.buscarPorId(id))
                .thenReturn(Optional.of(lote));

        ResponseEntity<Lote> respuesta =
                loteRestController.buscar(id);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(lote, respuesta.getBody());
    }

    // PRUEBA 3: Buscar un lote inexistente
    @Test
    void buscarLoteInexistente() {
        Long id = 999L;

        when(loteService.buscarPorId(id))
                .thenReturn(Optional.empty());

        ResponseEntity<Lote> respuesta =
                loteRestController.buscar(id);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());
    }

    // PRUEBA 4: Registrar un lote correctamente
    @Test
    void guardarLoteExitosamente() {
        Lote lote = new Lote();
        lote.setCodigo("TEST-001");

        when(loteService.guardar(lote)).thenReturn(lote);

        ResponseEntity<Lote> respuesta =
                loteRestController.guardar(lote);

        assertEquals(HttpStatus.CREATED,
                respuesta.getStatusCode());

        assertSame(lote, respuesta.getBody());
        verify(loteService).guardar(lote);
    }

    // PRUEBA 5: Actualizar un lote existente
    @Test
    void actualizarLoteExitosamente() {
        Long id = 1L;
        Lote lote = new Lote();
        lote.setCodigo("TEST-002");

        when(loteService.actualizar(id, lote))
                .thenReturn(lote);

        ResponseEntity<Lote> respuesta =
                loteRestController.actualizar(id, lote);

        assertEquals(HttpStatus.OK,
                respuesta.getStatusCode());

        assertSame(lote, respuesta.getBody());
        verify(loteService).actualizar(id, lote);
    }

    // PRUEBA 6: Actualizar un lote inexistente
    @Test
    void actualizarLoteInexistente() {
        Long id = 999L;
        Lote lote = new Lote();

        when(loteService.actualizar(id, lote))
                .thenThrow(new RuntimeException("Lote no encontrado"));

        ResponseEntity<Lote> respuesta =
                loteRestController.actualizar(id, lote);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());
    }

    // PRUEBA 7: Eliminar un lote existente
    @Test
    void eliminarLoteExitosamente() {
        Long id = 1L;

        ResponseEntity<Void> respuesta =
                loteRestController.eliminar(id);

        assertEquals(HttpStatus.NO_CONTENT,
                respuesta.getStatusCode());

        verify(loteService).eliminar(id);
    }

    // PRUEBA 8: Eliminar un lote inexistente
    @Test
    void eliminarLoteInexistente() {
        Long id = 999L;

        doThrow(new RuntimeException("Lote no encontrado"))
                .when(loteService).eliminar(id);

        ResponseEntity<Void> respuesta =
                loteRestController.eliminar(id);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());

        verify(loteService).eliminar(id);
    }
}
