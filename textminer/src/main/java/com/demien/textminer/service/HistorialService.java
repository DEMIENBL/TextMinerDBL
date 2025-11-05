package com.demien.textminer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HistorialService {

    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> obtenerHistorial() throws IOException {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        List<Map<String, Object>> lista = new ArrayList<>();

        Path carpeta = Paths.get("resultados");

        if (!Files.exists(carpeta)) {
            respuesta.put("mensaje", "No hay resultados guardados todavía.");
            return respuesta;
        }

        File[] archivos = carpeta.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (archivos == null || archivos.length == 0) {
            respuesta.put("mensaje", "No se encontraron archivos de análisis.");
            return respuesta;
        }

        Arrays.sort(archivos, Comparator.comparingLong(File::lastModified).reversed());

        for (File archivo : archivos) {
            try {
                JsonNode json = mapper.readTree(archivo);

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("archivo", archivo.getName());

                long lastModified = archivo.lastModified();
                item.put("fecha", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lastModified)));

                // Datos clave
                String sentimiento = json.path("sentimiento").path("sentimiento").asText("desconocido");
                item.put("sentimiento", sentimiento);

                List<String> temas = new ArrayList<>();
                if (json.has("temasDetectados")) {
                    for (JsonNode tema : json.get("temasDetectados")) {
                        if (tema.has("palabrasClave")) {
                            temas.add(tema.get("palabrasClave").get(0).asText());
                        }
                    }
                }
                item.put("temasPrincipales", temas);

                lista.add(item);
            } catch (Exception e) {
                System.err.println("Error leyendo archivo " + archivo.getName() + ": " + e.getMessage());
            }
        }

        respuesta.put("historial", lista);
        return respuesta;
    }
}

