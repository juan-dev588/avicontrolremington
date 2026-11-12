

package com.avicontrol;

import com.avicontrol.service.ClienteService;

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
class ClienteRestControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteRestController clienteRestController;

    // PRUEBA 1: Listar clientes exitosamente
    @Test
    void listarClientesExitosamente() {
        Cliente cliente1 = new Cliente();
        Cliente cliente2 = new Cliente();

        when(clienteService.listarTodos())
                .thenReturn(List.of(cliente1, cliente2));

        List<Cliente> resultado =
                clienteRestController.listar();

        assertEquals(2, resultado.size());
        verify(clienteService).listarTodos();
    }

    // PRUEBA 2: Buscar un cliente existente
    @Test
    void buscarClienteExistente() {
        Long id = 1L;
        Cliente cliente = new Cliente();

        when(clienteService.buscarPorId(id))
                .thenReturn(Optional.of(cliente));

        ResponseEntity<Cliente> respuesta =
                clienteRestController.buscar(id);

        assertEquals(HttpStatus.OK,
                respuesta.getStatusCode());

        assertSame(cliente, respuesta.getBody());
        verify(clienteService).buscarPorId(id);
    }

    // PRUEBA 3: Buscar un cliente inexistente
    @Test
    void buscarClienteInexistente() {
        Long id = 999L;

        when(clienteService.buscarPorId(id))
                .thenReturn(Optional.empty());

        ResponseEntity<Cliente> respuesta =
                clienteRestController.buscar(id);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());
    }

    // PRUEBA 4: Registrar un cliente correctamente
    @Test
    void guardarClienteExitosamente() {
        Cliente cliente = new Cliente();

        when(clienteService.guardar(cliente))
                .thenReturn(cliente);

        ResponseEntity<Cliente> respuesta =
                clienteRestController.guardar(cliente);

        assertEquals(HttpStatus.CREATED,
                respuesta.getStatusCode());

        assertSame(cliente, respuesta.getBody());
        verify(clienteService).guardar(cliente);
    }

    // PRUEBA 5: Actualizar un cliente existente
    @Test
    void actualizarClienteExitosamente() {
        Long id = 1L;
        Cliente cliente = new Cliente();

        when(clienteService.actualizar(id, cliente))
                .thenReturn(cliente);

        ResponseEntity<Cliente> respuesta =
                clienteRestController.actualizar(id, cliente);

        assertEquals(HttpStatus.OK,
                respuesta.getStatusCode());

        assertSame(cliente, respuesta.getBody());
        verify(clienteService).actualizar(id, cliente);
    }

    // PRUEBA 6: Actualizar un cliente inexistente
    @Test
    void actualizarClienteInexistente() {
        Long id = 999L;
        Cliente cliente = new Cliente();

        when(clienteService.actualizar(id, cliente))
                .thenThrow(
                        new RuntimeException("Cliente no encontrado")
                );

        ResponseEntity<Cliente> respuesta =
                clienteRestController.actualizar(id, cliente);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());

        verify(clienteService).actualizar(id, cliente);
    }

    // PRUEBA 7: Eliminar un cliente existente
    @Test
    void eliminarClienteExitosamente() {
        Long id = 1L;

        ResponseEntity<Void> respuesta =
                clienteRestController.eliminar(id);

        assertEquals(HttpStatus.NO_CONTENT,
                respuesta.getStatusCode());

        verify(clienteService).eliminar(id);
    }

    // PRUEBA 8: Eliminar un cliente inexistente
    @Test
    void eliminarClienteInexistente() {
        Long id = 999L;

        doThrow(
                new RuntimeException("Cliente no encontrado")
        ).when(clienteService).eliminar(id);

        ResponseEntity<Void> respuesta =
                clienteRestController.eliminar(id);

        assertEquals(HttpStatus.NOT_FOUND,
                respuesta.getStatusCode());

        verify(clienteService).eliminar(id);
    }
}
