package com.demien.textminer.service;

import opennlp.tools.namefind.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class NlpService {

    public Map<String, Object> procesarTexto(String texto) throws Exception {

        Map<String, Object> resultado = new LinkedHashMap<>();

        // === 1. Detectar oraciones (usando modelo en inglés) ===
        try (InputStream modeloSent = getClass().getResourceAsStream("/models/en-sent.bin")) {
            SentenceModel sentenceModel = new SentenceModel(modeloSent);
            SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
            String[] oraciones = detector.sentDetect(texto);
            resultado.put("oracionesDetectadas", oraciones);
        }

        // === 2. Tokenizar texto (usando modelo en inglés) ===
        String[] tokens;
        try (InputStream modeloTok = getClass().getResourceAsStream("/models/en-token.bin")) {
            TokenizerModel tokenModel = new TokenizerModel(modeloTok);
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            tokens = tokenizer.tokenize(texto);
            resultado.put("tokens", tokens);
        }

        // === 3. Reconocimiento de entidades (usando modelos en español) ===
        Map<String, List<String>> entidades = new HashMap<>();
        entidades.put("PERSON", detectarEntidades("/models/es-ner-person.bin", tokens));
        entidades.put("LOCATION", detectarEntidades("/models/es-ner-location.bin", tokens));
        entidades.put("ORGANIZATION", detectarEntidades("/models/es-ner-organization.bin", tokens));
        entidades.put("MISC", detectarEntidades("/models/es-ner-misc.bin", tokens));

        resultado.put("entidades", entidades);

        return resultado;
    }

    private List<String> detectarEntidades(String modeloPath, String[] tokens) throws Exception {
        List<String> entidades = new ArrayList<>();

        try (InputStream modelo = getClass().getResourceAsStream(modeloPath)) {
            if (modelo == null) {
                // Si no encuentra el archivo, evita que se rompa la API
                return entidades;
            }

            TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(modelo);
            NameFinderME nameFinder = new NameFinderME(nameFinderModel);
            Span[] spans = nameFinder.find(tokens);

            for (Span s : spans) {
                String entidad = String.join(" ", Arrays.copyOfRange(tokens, s.getStart(), s.getEnd()));
                entidades.add(entidad);
            }
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar el modelo: " + modeloPath);
        }

        return entidades;
    }
}

