package com.demien.textminer.service;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import cc.mallet.types.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ResumenTematicoService {

    @Autowired
    private LdaService ldaService;

    @Autowired
    private ResumenService resumenService;

    public Map<String, Object> generarResumenTematico(String texto) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();

        // === 1️⃣ Detectar temas con LDA ===
        Map<String, Object> ldaResultado = ldaService.analizarTemas(texto);
        List<Map<String, Object>> temas = (List<Map<String, Object>>) ldaResultado.get("temasDetectados");

        // === 2️⃣ Detectar oraciones ===
        String[] oraciones;
        try (InputStream modeloSent = getClass().getResourceAsStream("/models/en-sent.bin")) {
            SentenceModel sentenceModel = new SentenceModel(modeloSent);
            SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
            oraciones = detector.sentDetect(texto);
        }

        // === 3️⃣ Tokenizar texto ===
        String[] tokens;
        try (InputStream modeloTok = getClass().getResourceAsStream("/models/en-token.bin")) {
            TokenizerModel tokenModel = new TokenizerModel(modeloTok);
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            tokens = tokenizer.tokenize(texto.toLowerCase());
        }

        // === 4️⃣ Encontrar oración representativa por tema ===
        List<Map<String, Object>> resumenPorTema = new ArrayList<>();
        List<String> resumenGlobal = new ArrayList<>();

        for (Map<String, Object> tema : temas) {
            List<String> palabrasClave = (List<String>) tema.get("palabrasClave");
            String mejorOracion = null;
            double mejorScore = 0.0;

            for (String oracion : oraciones) {
                double score = 0.0;
                for (String palabra : palabrasClave) {
                    if (oracion.toLowerCase().contains(palabra.toLowerCase())) {
                        score += 1;
                    }
                }
                if (score > mejorScore) {
                    mejorScore = score;
                    mejorOracion = oracion;
                }
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tema", tema.get("tema"));
            item.put("palabrasClave", palabrasClave);
            item.put("fraseRepresentativa", mejorOracion != null ? mejorOracion : "Sin frase relevante.");
            resumenPorTema.add(item);

            if (mejorOracion != null) resumenGlobal.add(mejorOracion);
        }

        resultado.put("temasDetectados", resumenPorTema);
        resultado.put("resumenGlobal", resumenGlobal);

        return resultado;
    }
}
