package com.avicontrol;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class InicioController {

    private final LoteRepository loteRepository;
    private final GastoRepository gastoRepository;
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final PagoRepository pagoRepository;
    private final MortalidadRepository mortalidadRepository;

    public InicioController(
            LoteRepository loteRepository,
            GastoRepository gastoRepository,
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            PagoRepository pagoRepository,
            MortalidadRepository mortalidadRepository) {

        this.loteRepository = loteRepository;
        this.gastoRepository = gastoRepository;
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.pagoRepository = pagoRepository;
        this.mortalidadRepository = mortalidadRepository;
    }

    // ==================== INICIO ====================

    @GetMapping("/")
    public String inicio(Model model) {

        List<Lote> lotes = loteRepository.findAll();
        List<Gasto> gastos = gastoRepository.findAll();
        List<Venta> ventas = ventaRepository.findAll();
        List<Mortalidad> mortalidades = mortalidadRepository.findAll();

        BigDecimal totalGastos = gastos.stream()
                .map(Gasto::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costoPollitos = lotes.stream()
                .map(lote -> lote.getCostoPollitos() == null
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(lote.getCostoPollitos()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal inversionTotal = costoPollitos.add(totalGastos);

        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotalVenta)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gananciaNeta = totalVentas.subtract(inversionTotal);

        int pollosActivos = lotes.stream()
                .filter(lote -> "En crianza".equalsIgnoreCase(lote.getEstado()))
                .mapToInt(lote -> {

                    int iniciales = lote.getCantidadInicial() == null
                            ? 0 : lote.getCantidadInicial();

                    int muertos = mortalidades.stream()
                            .filter(m -> m.getLote() != null
                                    && Objects.equals(m.getLote().getId(), lote.getId()))
                            .map(Mortalidad::getCantidad)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .sum();

                    int vendidos = ventas.stream()
                            .filter(v -> v.getLote() != null
                                    && Objects.equals(v.getLote().getId(), lote.getId()))
                            .map(Venta::getCantidadPollos)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .sum();

                    return Math.max(0, iniciales - muertos - vendidos);
                })
                .sum();

        model.addAttribute("pollosActivos", pollosActivos);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("gananciaNeta", gananciaNeta);

        return "index";
    }

    // ==================== LOTES ====================

    @GetMapping("/lotes")
    public String lotes(Model model) {
        model.addAttribute("lotes", loteRepository.findAll());
        model.addAttribute("nuevoLote", new Lote());
        return "lotes";
    }

    @PostMapping("/lotes/guardar")
    public String guardarLote(
            @ModelAttribute("nuevoLote") Lote nuevoLote,
            RedirectAttributes redirectAttributes) {

        try {
            loteRepository.save(nuevoLote);
            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "¡Lote registrado correctamente!");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "No se pudo guardar. Verifica que el código no esté repetido y que los datos sean correctos.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "Ocurrió un error al guardar el lote.");
        }

        return "redirect:/lotes";
    }

    @GetMapping("/lotes/editar/{id}")
    public String mostrarEdicion(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return loteRepository.findById(id)
                .map(lote -> {
                    model.addAttribute("loteEditar", lote);
                    return "editar-lote";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "mensajeError", "El lote solicitado no existe.");
                    return "redirect:/lotes";
                });
    }

    @PostMapping("/lotes/actualizar/{id}")
    public String actualizarLote(
            @PathVariable Long id,
            @ModelAttribute("loteEditar") Lote datos,
            RedirectAttributes redirectAttributes) {

        try {
            Lote lote = loteRepository.findById(id).orElse(null);

            if (lote == null) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "El lote no existe.");
                return "redirect:/lotes";
            }

            lote.setCodigo(datos.getCodigo());
            lote.setFechaInicio(datos.getFechaInicio());
            lote.setCantidadInicial(datos.getCantidadInicial());
            lote.setCostoPollitos(datos.getCostoPollitos());
            lote.setEstado(datos.getEstado());
            lote.setObservaciones(datos.getObservaciones());

            loteRepository.save(lote);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Lote actualizado correctamente.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "Revisa si el código del lote ya existe.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "No se pudo actualizar el lote.");
        }

        return "redirect:/lotes";
    }

    @PostMapping("/lotes/eliminar/{id}")
    public String eliminarLote(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            if (!loteRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "El lote no existe.");
                return "redirect:/lotes";
            }

            loteRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Lote eliminado correctamente.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "No se puede eliminar este lote porque tiene registros relacionados.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "No se pudo eliminar el lote.");
        }

        return "redirect:/lotes";
    }

    @PostMapping("/lotes/finalizar/{id}")
    public String finalizarLote(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        return loteRepository.findById(id)
                .map(lote -> {
                    lote.setEstado("Finalizado");
                    loteRepository.save(lote);

                    redirectAttributes.addFlashAttribute(
                            "mensajeExito", "Lote finalizado correctamente.");

                    return "redirect:/lotes";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "mensajeError", "El lote no existe.");
                    return "redirect:/lotes";
                });
    }

    // ==================== DETALLE FINANCIERO DEL LOTE ====================

    @GetMapping("/lotes/detalle/{id}")
    public String detalleLote(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Lote lote = loteRepository.findById(id).orElse(null);

        if (lote == null) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "El lote no existe.");
            return "redirect:/lotes";
        }

        List<Gasto> gastos = gastoRepository.findByLoteIdOrderByFechaDesc(id);
        List<Venta> ventas = ventaRepository.findByLoteIdOrderByFechaDesc(id);
        List<Mortalidad> mortalidades = mortalidadRepository.findByLoteId(id);
        List<Pago> pagos = pagoRepository.findByVentaLoteId(id);

        BigDecimal totalGastos = gastos.stream()
                .map(Gasto::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costoPollitos = lote.getCostoPollitos() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(lote.getCostoPollitos());

        BigDecimal inversionTotal = costoPollitos.add(totalGastos);

        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotalVenta)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRecibido = pagos.stream()
                .map(Pago::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendienteCobrar = totalVentas.subtract(totalRecibido);

        BigDecimal ganancia = totalVentas.subtract(inversionTotal);

        int pollosMuertos = mortalidades.stream()
                .map(Mortalidad::getCantidad)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        int pollosVendidos = ventas.stream()
                .map(Venta::getCantidadPollos)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        int pollosIniciales = lote.getCantidadInicial() == null
                ? 0
                : lote.getCantidadInicial();

        int pollosDisponibles = Math.max(
                0, pollosIniciales - pollosMuertos - pollosVendidos);

        model.addAttribute("lote", lote);
        model.addAttribute("gastos", gastos);
        model.addAttribute("ventas", ventas);
        model.addAttribute("mortalidades", mortalidades);
        model.addAttribute("pagos", pagos);

        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("costoPollitos", costoPollitos);
        model.addAttribute("inversionTotal", inversionTotal);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalRecibido", totalRecibido);
        model.addAttribute("pendienteCobrar", pendienteCobrar);
        model.addAttribute("ganancia", ganancia);

        model.addAttribute("pollosIniciales", pollosIniciales);
        model.addAttribute("pollosMuertos", pollosMuertos);
        model.addAttribute("pollosVendidos", pollosVendidos);
        model.addAttribute("pollosDisponibles", pollosDisponibles);

        return "detalle-lote";
    }

    // ==================== GASTOS ====================

    @GetMapping("/gastos")
    public String gastos(
            @RequestParam(required = false) Long loteId,
            Model model) {

        var lotes = loteRepository.findAll();
        var todosLosGastos = gastoRepository.findAll();

        BigDecimal totalGeneral = todosLosGastos.stream()
                .map(Gasto::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Gasto> gastosFiltrados = todosLosGastos.stream()
                .filter(gasto -> loteId == null
                        || (gasto.getLote() != null
                        && Objects.equals(gasto.getLote().getId(), loteId)))
                .sorted(Comparator.comparing(
                        Gasto::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        BigDecimal totalSeleccionado = gastosFiltrados.stream()
                .map(Gasto::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Gasto nuevoGasto = new Gasto();
        nuevoGasto.setFecha(LocalDate.now());

        model.addAttribute("lotes", lotes);
        model.addAttribute("gastos", gastosFiltrados);
        model.addAttribute("nuevoGasto", nuevoGasto);
        model.addAttribute("loteSeleccionadoId", loteId);
        model.addAttribute("totalGeneral", totalGeneral);
        model.addAttribute("totalSeleccionado", totalSeleccionado);

        return "gastos";
    }

    @PostMapping("/gastos/guardar")
    public String guardarGasto(
            @ModelAttribute("nuevoGasto") Gasto nuevoGasto,
            @RequestParam("loteId") Long loteId,
            RedirectAttributes redirectAttributes) {

        try {
            Lote lote = loteRepository.findById(loteId).orElse(null);

            if (lote == null) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "Selecciona un lote válido.");
                return "redirect:/gastos";
            }

            if (nuevoGasto.getCategoria() == null
                    || nuevoGasto.getCategoria().isBlank()
                    || nuevoGasto.getDescripcion() == null
                    || nuevoGasto.getDescripcion().isBlank()
                    || nuevoGasto.getValor() == null
                    || nuevoGasto.getValor().compareTo(BigDecimal.ZERO) <= 0
                    || nuevoGasto.getFecha() == null) {

                redirectAttributes.addFlashAttribute(
                        "mensajeError",
                        "Completa los campos obligatorios y registra un valor mayor que cero.");
                return "redirect:/gastos";
            }

            nuevoGasto.setLote(lote);
            gastoRepository.save(nuevoGasto);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Gasto registrado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "Ocurrió un error al registrar el gasto.");
        }

        return "redirect:/gastos";
    }

    // ==================== VENTAS ====================

    @GetMapping("/ventas")
    public String ventas(
            @RequestParam(required = false) Long loteId,
            Model model) {

        List<Lote> lotes = loteRepository.findAll();
        List<Venta> todasLasVentas = ventaRepository.findAll();

        List<Venta> ventasFiltradas = todasLasVentas.stream()
                .filter(venta -> loteId == null
                        || (venta.getLote() != null
                        && Objects.equals(venta.getLote().getId(), loteId)))
                .sorted(Comparator.comparing(
                        Venta::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        BigDecimal totalGeneral = todasLasVentas.stream()
                .map(Venta::getTotalVenta)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSeleccionado = ventasFiltradas.stream()
                .map(Venta::getTotalVenta)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDate.now());
        nuevaVenta.setCantidadPollos(1);
        nuevaVenta.setPesoTotal(BigDecimal.ZERO);
        nuevaVenta.setPrecioPorKilo(new BigDecimal("12000"));
        nuevaVenta.setTotalVenta(BigDecimal.ZERO);

        model.addAttribute("lotes", lotes);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("ventas", ventasFiltradas);
        model.addAttribute("nuevaVenta", nuevaVenta);
        model.addAttribute("loteSeleccionadoId", loteId);
        model.addAttribute("totalGeneral", totalGeneral);
        model.addAttribute("totalSeleccionado", totalSeleccionado);

        return "ventas";
    }

    @PostMapping("/ventas/guardar")
    public String guardarVenta(
            @ModelAttribute("nuevaVenta") Venta nuevaVenta,
            @RequestParam("loteId") Long loteId,
            @RequestParam(required = false) Long clienteId,
            RedirectAttributes redirectAttributes) {

        try {
            Lote lote = loteRepository.findById(loteId).orElse(null);

            if (lote == null) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "Selecciona un lote válido.");
                return "redirect:/ventas";
            }

            if (nuevaVenta.getCantidadPollos() == null
                    || nuevaVenta.getCantidadPollos() <= 0
                    || nuevaVenta.getPesoTotal() == null
                    || nuevaVenta.getPesoTotal().compareTo(BigDecimal.ZERO) <= 0
                    || nuevaVenta.getPrecioPorKilo() == null
                    || nuevaVenta.getPrecioPorKilo().compareTo(BigDecimal.ZERO) <= 0
                    || nuevaVenta.getFecha() == null) {

                redirectAttributes.addFlashAttribute(
                        "mensajeError",
                        "Verifica la cantidad de pollos, el peso total, el precio por kilo y la fecha.");
                return "redirect:/ventas";
            }

            BigDecimal total = nuevaVenta.getPesoTotal()
                    .multiply(nuevaVenta.getPrecioPorKilo())
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            nuevaVenta.setTotalVenta(total);
            nuevaVenta.setLote(lote);

            if (clienteId != null) {
                Cliente cliente = clienteRepository.findById(clienteId)
                        .orElse(null);

                if (cliente == null) {
                    redirectAttributes.addFlashAttribute(
                            "mensajeError", "El cliente seleccionado no existe.");
                    return "redirect:/ventas";
                }

                nuevaVenta.setCliente(cliente);
            } else {
                nuevaVenta.setCliente(null);
            }

            ventaRepository.save(nuevaVenta);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Venta registrada correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "Ocurrió un error al registrar la venta.");
        }

        return "redirect:/ventas";
    }

    // ==================== CUENTAS POR COBRAR ====================

    @GetMapping("/cuentas")
    public String cuentas(Model model) {

        List<Venta> ventas = ventaRepository.findAllByOrderByFechaDesc();

        List<Venta> ventasPendientes = ventas.stream()
                .filter(venta -> {
                    BigDecimal recibido = pagoRepository
                            .findByVentaId(venta.getId())
                            .stream()
                            .map(Pago::getValor)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return venta.getTotalVenta() != null
                            && venta.getTotalVenta().compareTo(recibido) > 0;
                })
                .collect(Collectors.toList());

        BigDecimal totalVendido = ventas.stream()
                .map(Venta::getTotalVenta)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRecibido = BigDecimal.ZERO;

        for (Venta venta : ventas) {
            BigDecimal recibido = pagoRepository
                    .findByVentaId(venta.getId())
                    .stream()
                    .map(Pago::getValor)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalRecibido = totalRecibido.add(recibido);
        }

        BigDecimal pendiente = totalVendido.subtract(totalRecibido);

        model.addAttribute("ventasPendientes", ventasPendientes);
        model.addAttribute("totalVendido", totalVendido);
        model.addAttribute("totalRecibido", totalRecibido);
        model.addAttribute("pendiente", pendiente);

        return "cuentas";
    }

    // ==================== REGISTRAR PAGOS ====================

    @PostMapping("/pagos/guardar")
    public String guardarPago(
            @RequestParam Long ventaId,
            @RequestParam BigDecimal valor,
            RedirectAttributes redirectAttributes) {

        try {
            Venta venta = ventaRepository.findById(ventaId).orElse(null);

            if (venta == null) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "La venta no existe.");
                return "redirect:/cuentas";
            }

            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "El abono debe ser mayor que cero.");
                return "redirect:/cuentas";
            }

            BigDecimal recibido = pagoRepository.findByVentaId(ventaId)
                    .stream()
                    .map(Pago::getValor)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal pendiente = venta.getTotalVenta().subtract(recibido);

            if (valor.compareTo(pendiente) > 0) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError",
                        "El abono supera el saldo pendiente de esta venta.");
                return "redirect:/cuentas";
            }

            Pago pago = new Pago();
            pago.setVenta(venta);
            pago.setValor(valor);
            pago.setFecha(LocalDate.now());

            pagoRepository.save(pago);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Abono registrado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "No se pudo registrar el abono.");
        }

        return "redirect:/cuentas";
    }

    // ==================== MORTALIDAD ====================

    @GetMapping("/mortalidad")
    public String mortalidad(
            @RequestParam(required = false) Long loteId,
            Model model) {

        List<Lote> lotes = loteRepository.findAll();

        List<Mortalidad> registros = mortalidadRepository.findAll()
                .stream()
                .filter(m -> loteId == null
                        || (m.getLote() != null
                        && Objects.equals(m.getLote().getId(), loteId)))
                .sorted(Comparator.comparing(
                        Mortalidad::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        Mortalidad nuevoRegistro = new Mortalidad();
        nuevoRegistro.setFecha(LocalDate.now());

        int totalMuertos = registros.stream()
                .map(Mortalidad::getCantidad)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        model.addAttribute("lotes", lotes);
        model.addAttribute("mortalidades", registros);
        model.addAttribute("nuevoRegistro", nuevoRegistro);
        model.addAttribute("loteSeleccionadoId", loteId);
        model.addAttribute("totalMuertos", totalMuertos);

        return "mortalidad";
    }

    @PostMapping("/mortalidad/guardar")
    public String guardarMortalidad(
            @ModelAttribute("nuevoRegistro") Mortalidad nuevoRegistro,
            @RequestParam Long loteId,
            RedirectAttributes redirectAttributes) {

        try {
            Lote lote = loteRepository.findById(loteId).orElse(null);

            if (lote == null) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "Selecciona un lote válido.");
                return "redirect:/mortalidad";
            }

            if (nuevoRegistro.getCantidad() == null
                    || nuevoRegistro.getCantidad() <= 0
                    || nuevoRegistro.getFecha() == null) {

                redirectAttributes.addFlashAttribute(
                        "mensajeError",
                        "Registra una cantidad mayor que cero y una fecha válida.");
                return "redirect:/mortalidad";
            }

            nuevoRegistro.setLote(lote);
            mortalidadRepository.save(nuevoRegistro);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Mortalidad registrada correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "No se pudo registrar la mortalidad.");
        }

        return "redirect:/mortalidad";
    }

    // ==================== CLIENTES ====================

    @GetMapping("/clientes")
    public String clientes(
            @RequestParam(required = false) String buscar,
            Model model) {

        List<Cliente> todosLosClientes = clienteRepository.findAll();

        List<Cliente> clientesFiltrados = todosLosClientes.stream()
                .filter(cliente -> buscar == null || buscar.isBlank()
                        || (cliente.getNombre() != null
                        && cliente.getNombre().toLowerCase()
                        .contains(buscar.trim().toLowerCase()))
                        || (cliente.getTelefono() != null
                        && cliente.getTelefono().contains(buscar.trim())))
                .sorted(Comparator.comparing(
                        Cliente::getNombre,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());

        model.addAttribute("clientes", clientesFiltrados);
        model.addAttribute("nuevoCliente", new Cliente());
        model.addAttribute("buscar", buscar);

        return "clientes";
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(
            @ModelAttribute("nuevoCliente") Cliente nuevoCliente,
            RedirectAttributes redirectAttributes) {

        try {
            if (nuevoCliente.getNombre() == null
                    || nuevoCliente.getNombre().isBlank()) {

                redirectAttributes.addFlashAttribute(
                        "mensajeError", "El nombre del cliente es obligatorio.");
                return "redirect:/clientes";
            }

            nuevoCliente.setNombre(nuevoCliente.getNombre().trim());
            clienteRepository.save(nuevoCliente);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Cliente registrado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "Ocurrió un error al registrar el cliente.");
        }

        return "redirect:/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return clienteRepository.findById(id)
                .map(cliente -> {
                    model.addAttribute("clienteEditar", cliente);
                    return "editar-cliente";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "mensajeError", "El cliente solicitado no existe.");
                    return "redirect:/clientes";
                });
    }

    @PostMapping("/clientes/actualizar/{id}")
    public String actualizarCliente(
            @PathVariable Long id,
            @ModelAttribute("clienteEditar") Cliente datos,
            RedirectAttributes redirectAttributes) {

        try {
            Cliente cliente = clienteRepository.findById(id).orElse(null);

            if (cliente == null) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "El cliente no existe.");
                return "redirect:/clientes";
            }

            if (datos.getNombre() == null || datos.getNombre().isBlank()) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "El nombre del cliente es obligatorio.");
                return "redirect:/clientes";
            }

            cliente.setNombre(datos.getNombre().trim());
            cliente.setTelefono(datos.getTelefono());
            cliente.setDireccion(datos.getDireccion());
            cliente.setObservaciones(datos.getObservaciones());

            clienteRepository.save(cliente);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Cliente actualizado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "No se pudo actualizar el cliente.");
        }

        return "redirect:/clientes";
    }

    @PostMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            if (!clienteRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute(
                        "mensajeError", "El cliente no existe.");
                return "redirect:/clientes";
            }

            clienteRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito", "Cliente eliminado correctamente.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "No se puede eliminar este cliente porque tiene ventas relacionadas.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError", "No se pudo eliminar el cliente.");
        }

        return "redirect:/clientes";
    }
}