package com.demien.textminer.service;

import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumenService {

    // Lista de palabras comunes que no aportan significado (stopwords)
    private static final Set<String> STOPWORDS = Set.of(
        "el", "la", "los", "las", "de", "del", "en", "y", "a", "un", "una",
        "por", "con", "para", "que", "es", "al", "como", "se", "su", "sus",
        "o", "u", "pero", "si", "no", "lo", "ya", "muy", "más", "menos"
    );

    public Map<String, Object> generarResumen(String texto) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();

        // === 1. Detectar oraciones ===
        String[] oraciones;
        try (InputStream modeloSent = getClass().getResourceAsStream("/models/en-sent.bin")) {
            SentenceModel sentenceModel = new SentenceModel(modeloSent);
            SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
            oraciones = detector.sentDetect(texto);
        }

        // === 2. Tokenizar texto ===
        String[] tokens;
        try (InputStream modeloTok = getClass().getResourceAsStream("/models/en-token.bin")) {
            TokenizerModel tokenModel = new TokenizerModel(modeloTok);
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            tokens = tokenizer.tokenize(texto.toLowerCase());
        }

        // === 3. Contar frecuencia de palabras relevantes ===
        Map<String, Integer> frecuencia = new HashMap<>();
        for (String token : tokens) {
            if (token.matches("[a-záéíóúñ]+") && !STOPWORDS.contains(token)) {
                frecuencia.put(token, frecuencia.getOrDefault(token, 0) + 1);
            }
        }

        // === 4. Calcular puntuación de cada oración ===
        Map<String, Double> puntuaciones = new HashMap<>();
        for (String oracion : oraciones) {
            double score = 0;
            for (String palabra : frecuencia.keySet()) {
                if (oracion.toLowerCase().contains(palabra)) {
                    score += frecuencia.get(palabra);
                }
            }
            puntuaciones.put(oracion, score);
        }

        // === 5. Seleccionar las oraciones con mayor puntuación ===
        List<Map.Entry<String, Double>> ordenadas = puntuaciones.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        int topN = Math.max(1, oraciones.length / 3); // resumir al 30% del texto
        List<String> resumen = ordenadas.stream()
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // === 6. Armar resultado ===
        resultado.put("oracionesTotales", oraciones.length);
        resultado.put("palabrasRelevantes", frecuencia);
        resultado.put("resumen", resumen);

        return resultado;
    }
}
