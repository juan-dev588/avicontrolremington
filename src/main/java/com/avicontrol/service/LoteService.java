package com.avicontrol.service;

import com.avicontrol.Lote;
import com.avicontrol.LoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoteService {

    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    // Registrar un lote
    public Lote guardar(Lote lote) {
        return loteRepository.save(lote);
    }

    // Consultar todos los lotes
    public List<Lote> listarTodos() {
        return loteRepository.findAll();
    }

    // Buscar un lote por su ID
    public Optional<Lote> buscarPorId(Long id) {
        return loteRepository.findById(id);
    }

    // Buscar un lote por su código
    public Optional<Lote> buscarPorCodigo(String codigo) {
        return loteRepository.findByCodigo(codigo);
    }

    // Actualizar un lote
    public Lote actualizar(Long id, Lote datos) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        lote.setCodigo(datos.getCodigo());
        lote.setFechaInicio(datos.getFechaInicio());
        lote.setCantidadInicial(datos.getCantidadInicial());
        lote.setCostoPollitos(datos.getCostoPollitos());
        lote.setEstado(datos.getEstado());
        lote.setObservaciones(datos.getObservaciones());

        return loteRepository.save(lote);
    }

    // Eliminar un lote
    public void eliminar(Long id) {
        if (!loteRepository.existsById(id)) {
            throw new RuntimeException("Lote no encontrado");
        }

        loteRepository.deleteById(id);
    }
}