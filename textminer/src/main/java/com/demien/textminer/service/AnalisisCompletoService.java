package com.demien.textminer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AnalisisCompletoService {

    @Autowired
    private AnalisisService analisisService;

    @Autowired
    private NlpService nlpService;

    @Autowired
    private ResumenService resumenService;

    @Autowired
    private LdaService ldaService;

    @Autowired
    private ResumenTematicoService resumenTematicoService;

    public Map<String, Object> analizarTodo(String texto) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();

        // === 1️⃣ Análisis de sentimiento ===
        var analisisSentimiento = analisisService.analizarTexto(texto);
        resultado.put("sentimiento", Map.of(
                "totalPalabras", analisisSentimiento.getTotalPalabras(),
                "positivas", analisisSentimiento.getPositivas(),
                "negativas", analisisSentimiento.getNegativas(),
                "sentimiento", analisisSentimiento.getSentimiento()
        ));

        // === 2️⃣ Entidades ===
        Map<String, Object> entidades = nlpService.procesarTexto(texto);
        resultado.put("entidades", entidades.get("entidades"));

        // === 3️⃣ Resumen clásico ===
        Map<String, Object> resumenClasico = resumenService.generarResumen(texto);
        resultado.put("resumenClasico", resumenClasico.get("resumen"));

        // === 4️⃣ Temas LDA ===
        Map<String, Object> ldaResultado = ldaService.analizarTemas(texto);
        resultado.put("temasDetectados", ldaResultado.get("temasDetectados"));

        // === 5️⃣ Resumen temático ===
        Map<String, Object> resumenTematico = resumenTematicoService.generarResumenTematico(texto);
        resultado.put("resumenTematico", resumenTematico.get("temasDetectados"));
        resultado.put("resumenGlobal", resumenTematico.get("resumenGlobal"));

        // === 6️⃣ Información adicional ===
        resultado.put("longitudTexto", texto.split("\\s+").length);
        resultado.put("versionModelo", "TextMinerDBL v6.0");
        resultado.put("autor", "Demien Becerra Lozano");

        // === 7️⃣ Guardar resultado como JSON ===
        guardarResultado(resultado);

        return resultado;
    }

    private void guardarResultado(Map<String, Object> resultado) throws IOException {
        // Crear carpeta /resultados si no existe
        Path carpeta = Paths.get("resultados");
        if (!Files.exists(carpeta)) {
            Files.createDirectory(carpeta);
        }

        // Nombre del archivo con timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo = "analisis_" + timestamp + ".json";
        Path rutaArchivo = carpeta.resolve(nombreArchivo);

        // Convertir el resultado a JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(rutaArchivo.toFile(), resultado);

        System.out.println("✅ Análisis guardado en: " + rutaArchivo.toAbsolutePath());
    }
}
