
package com.avicontrol;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(name = "cantidad_pollos", nullable = false)
    private Integer cantidadPollos;

    @Column(name = "peso_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal pesoTotal;

    @Column(name = "precio_por_kilo", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioPorKilo;

    @Column(name = "total_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalVenta;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    public Venta() {
    }

    public Long getId() {
        return id;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Integer getCantidadPollos() {
        return cantidadPollos;
    }

    public void setCantidadPollos(Integer cantidadPollos) {
        this.cantidadPollos = cantidadPollos;
    }

    public BigDecimal getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(BigDecimal pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public BigDecimal getPrecioPorKilo() {
        return precioPorKilo;
    }

    public void setPrecioPorKilo(BigDecimal precioPorKilo) {
        this.precioPorKilo = precioPorKilo;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
