
document.addEventListener("DOMContentLoaded", function () {
    const botonRegistrar = document.getElementById("btnRegistrarLote");
    const modal = document.getElementById("modalLote");
    const botonCerrar = document.getElementById("btnCerrarModalLote");
    const botonCancelar = document.getElementById("btnCancelarLote");
    const formulario = document.getElementById("formularioLote");

    // Si esta página no tiene el formulario, no hacemos nada.
    if (!botonRegistrar || !modal) {
        return;
    }

    // Abrir el formulario.
    function abrirFormulario() {
        modal.hidden = false;
        document.body.style.overflow = "hidden";

        const campoCodigo = document.getElementById("codigoLote");
        if (campoCodigo) {
            campoCodigo.focus();
        }
    }

    // Cerrar el formulario.
    function cerrarFormulario() {
        modal.hidden = true;
        document.body.style.overflow = "";
    }

    botonRegistrar.addEventListener("click", abrirFormulario);

    if (botonCerrar) {
        botonCerrar.addEventListener("click", cerrarFormulario);
    }

    if (botonCancelar) {
        botonCancelar.addEventListener("click", cerrarFormulario);
    }

    // Cerrar al pulsar fuera del formulario.
    modal.addEventListener("click", function (evento) {
        if (evento.target === modal) {
            cerrarFormulario();
        }
    });

    // Cerrar con Escape.
    document.addEventListener("keydown", function (evento) {
        if (evento.key === "Escape" && !modal.hidden) {
            cerrarFormulario();
        }
    });

    // Enviar los datos al controlador de Spring Boot.
    // No mostramos alertas falsas ni bloqueamos el envío.
    if (formulario) {
        formulario.addEventListener("submit", function (evento) {
            const botonGuardar = formulario.querySelector(
                'button[type="submit"]'
            );

            // Evitar enviar el formulario si faltan datos obligatorios.
            if (!formulario.checkValidity()) {
                evento.preventDefault();
                formulario.reportValidity();
                return;
            }

            // Permitir el envío normal del formulario a Spring Boot.
            if (botonGuardar) {
                botonGuardar.disabled = true;
                botonGuardar.textContent = "Guardando...";
            }
        });
    }
});
