
package com.avicontrol.service;

import com.avicontrol.Venta;
import com.avicontrol.VentaRepository;
import com.avicontrol.LoteRepository;
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
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private VentaService ventaService;

    // PRUEBA 1: Guardar una venta correctamente
    @Test
    void guardarVentaExitosamente() {
        Venta venta = new Venta();

        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.guardar(venta);

        assertNotNull(resultado);
        assertSame(venta, resultado);

        verify(ventaRepository).save(venta);
    }

    // PRUEBA 2: Consultar todas las ventas
    @Test
    void listarVentasExitosamente() {
        Venta venta1 = new Venta();
        Venta venta2 = new Venta();

        when(ventaRepository.findAllByOrderByFechaDesc())
                .thenReturn(List.of(venta1, venta2));

        List<Venta> resultado = ventaService.listarTodos();

        assertEquals(2, resultado.size());

        verify(ventaRepository).findAllByOrderByFechaDesc();
    }

    // PRUEBA 3: Buscar una venta por ID
    @Test
    void buscarVentaPorIdExitosamente() {
        Long id = 1L;
        Venta venta = new Venta();

        when(ventaRepository.findById(id))
                .thenReturn(Optional.of(venta));

        Optional<Venta> resultado = ventaService.buscarPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(venta, resultado.get());

        verify(ventaRepository).findById(id);
    }

    // PRUEBA 4: Eliminar una venta correctamente
    @Test
    void eliminarVentaExitosamente() {
        Long id = 1L;

        when(ventaRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> ventaService.eliminar(id));

        verify(ventaRepository).existsById(id);
        verify(ventaRepository).deleteById(id);
    }

    // PRUEBA 5: Eliminar una venta inexistente
    @Test
    void eliminarVentaInexistente() {
        Long id = 999L;

        when(ventaRepository.existsById(id)).thenReturn(false);

        RuntimeException error = assertThrows(
            RuntimeException.class,
            () -> ventaService.eliminar(id)
        );

        assertEquals("Venta no encontrada", error.getMessage());

        verify(ventaRepository).existsById(id);
        verify(ventaRepository, never()).deleteById(id);
    }
}

