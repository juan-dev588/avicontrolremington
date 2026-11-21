
package com.avicontrol;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/chatbot")
public class GeminiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

   private static final String URL_GEMINI =
    "https://generativelanguage.googleapis.com/v1beta/models/"
    + "gemini-3.1-flash-lite:generateContent";

    @PostMapping
    public ResponseEntity<Map<String, String>> preguntar(
            @RequestBody Map<String, String> datos) {

        String pregunta = datos.getOrDefault("pregunta", "").trim();

        if (pregunta.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "respuesta",
                            "Por favor, escribe una pregunta."
                    ));
        }

        if (pregunta.length() > 2000) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "respuesta",
                            "La pregunta es demasiado larga."
                    ));
        }

        String instrucciones = """
                Eres el Asistente Avícola IA de AviControl.

                Responde siempre en español claro, sencillo
                y fácil de entender.

                Eres especialista en pollos de engorde.

                Puedes responder preguntas sobre:

                - Alimentación de pollos de engorde.
                - Consumo de concentrado por edad.
                - Peso y crecimiento de los pollos.
                - Vitaminas y vacunación.
                - Enfermedades y prevención.
                - Temperatura y ventilación del galpón.
                - Limpieza y bioseguridad.
                - Mortalidad y bienestar animal.
                - Costos de producción.
                - Ventas y rentabilidad del negocio.

                Da recomendaciones prácticas y explica
                paso a paso cuando sea necesario.

                No inventes datos de la granja ni afirmes
                que tienes acceso a la base de datos.

                Si preguntan por enfermedades o medicamentos,
                recomienda consultar a un veterinario.
                No indiques dosis de antibióticos sin
                valoración profesional.
                """;

        try {

            Map<String, Object> cuerpo = Map.of(
                    "systemInstruction", Map.of(
                            "parts", new Object[]{
                                    Map.of("text", instrucciones)
                            }
                    ),
                    "contents", new Object[]{
                            Map.of(
                                    "role", "user",
                                    "parts", new Object[]{
                                            Map.of("text", pregunta)
                                    }
                            )
                    }
            );

            String json = mapper.writeValueAsString(cuerpo);

            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(URL_GEMINI))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .timeout(Duration.ofSeconds(120))
                    .POST(
                            HttpRequest.BodyPublishers.ofString(
                                    json,
                                    StandardCharsets.UTF_8
                            )
                    )
                    .build();

            HttpResponse<String> respuesta = cliente.send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (respuesta.statusCode() != 200) {

                System.err.println(
                        "Error de Gemini. Código HTTP: "
                        + respuesta.statusCode()
                );

                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Map.of(
                                "respuesta",
                                "No pude consultar Gemini. "
                                + "Revisa la configuración de la API."
                        ));
            }

            JsonNode resultado = mapper.readTree(respuesta.body());

            JsonNode texto = resultado.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (texto.isMissingNode() || texto.stringValue().isBlank()) {

                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Map.of(
                                "respuesta",
                                "Gemini no devolvió una respuesta."
                        ));
            }

            return ResponseEntity.ok(
                    Map.of(
                            "respuesta",
                           texto.stringValue()
                    )
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return ResponseEntity.status(
                    HttpStatus.SERVICE_UNAVAILABLE
            ).body(Map.of(
                    "respuesta",
                    "La consulta fue interrumpida."
            ));

        } catch (Exception e) {

            System.err.println(
                    "Error al conectar con Gemini: "
                    + e.getClass().getSimpleName()
            );

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "respuesta",
                            "No fue posible conectar con Gemini."
                    ));
        }
    }
}
