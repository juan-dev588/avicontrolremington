
package com.avicontrol.service;

import com.avicontrol.Lote;
import com.avicontrol.LoteRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private LoteService loteService;

    // PRUEBA 1: Guardar un lote correctamente
    @Test
    void guardarLoteExitosamente() {
        Lote lote = new Lote();
        lote.setCodigo("TEST-001");

        when(loteRepository.save(lote)).thenReturn(lote);

        Lote resultado = loteService.guardar(lote);

        assertNotNull(resultado);
        assertEquals("TEST-001", resultado.getCodigo());

        verify(loteRepository).save(lote);
    }

    // PRUEBA 2: Eliminar un lote inexistente
    @Test
    void eliminarLoteInexistente() {
        Long id = 999L;

        when(loteRepository.existsById(id)).thenReturn(false);

        RuntimeException error = assertThrows(
            RuntimeException.class,
            () -> loteService.eliminar(id)
        );

        assertEquals("Lote no encontrado", error.getMessage());

        verify(loteRepository).existsById(id);
        verify(loteRepository, never()).deleteById(id);
    }
}

