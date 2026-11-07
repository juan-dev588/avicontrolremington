
package com.avicontrol.service;

import com.avicontrol.Gasto;
import com.avicontrol.GastoRepository;
import com.avicontrol.LoteRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private GastoService gastoService;

    // PRUEBA 1: Guardar un gasto correctamente
    @Test
    void guardarGastoExitosamente() {
        Gasto gasto = new Gasto();

        when(gastoRepository.save(gasto)).thenReturn(gasto);

        Gasto resultado = gastoService.guardar(gasto);

        assertNotNull(resultado);
        assertSame(gasto, resultado);
        verify(gastoRepository).save(gasto);
    }

    // PRUEBA 2: Consultar todos los gastos
    @Test
    void listarGastosExitosamente() {
        Gasto gasto1 = new Gasto();
        Gasto gasto2 = new Gasto();

        when(gastoRepository.findAll())
                .thenReturn(List.of(gasto1, gasto2));

        List<Gasto> resultado = gastoService.listarTodos();

        assertEquals(2, resultado.size());
        verify(gastoRepository).findAll();
    }

    // PRUEBA 3: Intentar eliminar un gasto inexistente
    @Test
    void eliminarGastoInexistente() {
        Long id = 999L;

        when(gastoRepository.existsById(id)).thenReturn(false);

        RuntimeException error = assertThrows(
            RuntimeException.class,
            () -> gastoService.eliminar(id)
        );

        assertEquals("Gasto no encontrado", error.getMessage());

        verify(gastoRepository).existsById(id);
        verify(gastoRepository, never()).deleteById(id);
    }
}
