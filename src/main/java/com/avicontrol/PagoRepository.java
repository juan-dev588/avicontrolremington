
package com.avicontrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByVentaId(Long ventaId);

    List<Pago> findByVentaLoteId(Long loteId);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pago p WHERE p.venta.lote.id = :loteId")
    BigDecimal sumarPagosPorLote(@Param("loteId") Long loteId);
}
