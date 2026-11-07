package com.avicontrol;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByLoteIdOrderByFechaDesc(Long loteId);

    List<Venta> findAllByOrderByFechaDesc();
}