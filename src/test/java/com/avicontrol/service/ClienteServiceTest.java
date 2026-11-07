
package com.avicontrol.service;

import com.avicontrol.Cliente;
import com.avicontrol.ClienteRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    // PRUEBA 1: Guardar un cliente correctamente
    @Test
    void guardarClienteExitosamente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente de prueba");

        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.guardar(cliente);

        assertNotNull(resultado);
        assertEquals("Cliente de prueba", resultado.getNombre());

        verify(clienteRepository).save(cliente);
    }

    // PRUEBA 2: Consultar todos los clientes
    @Test
    void listarClientesExitosamente() {
        Cliente cliente1 = new Cliente();
        Cliente cliente2 = new Cliente();

        when(clienteRepository.findAll())
                .thenReturn(List.of(cliente1, cliente2));

        List<Cliente> resultado = clienteService.listarTodos();

        assertEquals(2, resultado.size());
        verify(clienteRepository).findAll();
    }

    // PRUEBA 3: Buscar un cliente por ID
    @Test
    void buscarClientePorIdExitosamente() {
        Long id = 1L;

        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente de prueba");

        when(clienteRepository.findById(id))
                .thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.buscarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals("Cliente de prueba",
                resultado.get().getNombre());

        verify(clienteRepository).findById(id);
    }

    // PRUEBA 4: Eliminar un cliente inexistente
    @Test
    void eliminarClienteInexistente() {
        Long id = 999L;

        when(clienteRepository.existsById(id))
                .thenReturn(false);

        RuntimeException error = assertThrows(
            RuntimeException.class,
            () -> clienteService.eliminar(id)
        );

        assertEquals("Cliente no encontrado",
                error.getMessage());

        verify(clienteRepository).existsById(id);
        verify(clienteRepository, never()).deleteById(id);
    }
}
