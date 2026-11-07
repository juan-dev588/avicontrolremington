package com.avicontrol.service;

import com.avicontrol.Gasto;
import com.avicontrol.GastoRepository;
import com.avicontrol.Lote;
import com.avicontrol.LoteRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GastoService {

    private final GastoRepository gastoRepository;
    private final LoteRepository loteRepository;

    public GastoService(
            GastoRepository gastoRepository,
            LoteRepository loteRepository) {
        this.gastoRepository = gastoRepository;
        this.loteRepository = loteRepository;
    }

    public Gasto guardar(Gasto gasto) {
        return gastoRepository.save(gasto);
    }

    public List<Gasto> listarTodos() {
        return gastoRepository.findAll();
    }

    public Optional<Gasto> buscarPorId(Long id) {
        return gastoRepository.findById(id);
    }

    public List<Gasto> listarPorLote(Long loteId) {
        return gastoRepository.findByLoteIdOrderByFechaDesc(loteId);
    }

    public Gasto actualizar(Long id, Gasto datos) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        Lote lote = loteRepository.findById(datos.getLote().getId())
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        gasto.setLote(lote);
        gasto.setCategoria(datos.getCategoria());
        gasto.setDescripcion(datos.getDescripcion());
        gasto.setValor(datos.getValor());
        gasto.setFecha(datos.getFecha());
        gasto.setObservaciones(datos.getObservaciones());

        return gastoRepository.save(gasto);
    }

    public void eliminar(Long id) {
        if (!gastoRepository.existsById(id)) {
            throw new RuntimeException("Gasto no encontrado");
        }

        gastoRepository.deleteById(id);
    }
}