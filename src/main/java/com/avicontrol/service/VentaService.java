package com.avicontrol.service;

import com.avicontrol.Venta;
import com.avicontrol.VentaRepository;
import com.avicontrol.Lote;
import com.avicontrol.LoteRepository;
import com.avicontrol.Cliente;
import com.avicontrol.ClienteRepository;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final LoteRepository loteRepository;
    private final ClienteRepository clienteRepository;

    public VentaService(
            VentaRepository ventaRepository,
            LoteRepository loteRepository,
            ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.loteRepository = loteRepository;
        this.clienteRepository = clienteRepository;
    }

    public Venta guardar(Venta venta) {
        return ventaRepository.save(venta);
    }

    public List<Venta> listarTodos() {
        return ventaRepository.findAllByOrderByFechaDesc();
    }

    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public List<Venta> listarPorLote(Long loteId) {
        return ventaRepository.findByLoteIdOrderByFechaDesc(loteId);
    }

    public Venta actualizar(Long id, Venta datos) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        Lote lote = loteRepository.findById(datos.getLote().getId())
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        venta.setLote(lote);
        venta.setCantidadPollos(datos.getCantidadPollos());
        venta.setPesoTotal(datos.getPesoTotal());
        venta.setPrecioPorKilo(datos.getPrecioPorKilo());
        venta.setTotalVenta(datos.getTotalVenta());
        venta.setFecha(datos.getFecha());
        venta.setObservaciones(datos.getObservaciones());

        if (datos.getCliente() != null) {
            Cliente cliente = clienteRepository.findById(datos.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            venta.setCliente(cliente);
        } else {
            venta.setCliente(null);
        }

        return ventaRepository.save(venta);
    }

    public void eliminar(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }

        ventaRepository.deleteById(id);
    }
}