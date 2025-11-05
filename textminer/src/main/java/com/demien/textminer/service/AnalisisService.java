package com.demien.textminer.service;

import com.demien.textminer.model.ResultadoAnalisis;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AnalisisService {

    private final Set<String> positivas = Set.of("excelente", "bueno", "genial", "fantástico");
    private final Set<String> negativas = Set.of("malo", "lento", "terrible", "horrible");

    public ResultadoAnalisis analizarTexto(String texto) {
        // Normalizar texto
        texto = texto.toLowerCase()
                .replaceAll("[^\\p{L}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String[] palabras = texto.split(" ");
        List<String> pos = new ArrayList<>();
        List<String> neg = new ArrayList<>();

        for (String p : palabras) {
            if (positivas.contains(p)) pos.add(p);
            if (negativas.contains(p)) neg.add(p);
        }

        String sentimiento = "neutral";
        if (pos.size() > neg.size()) sentimiento = "positivo";
        else if (neg.size() > pos.size()) sentimiento = "negativo";
        else if (!pos.isEmpty() && !neg.isEmpty()) sentimiento = "mixto";

        // Devolver objeto limpio
        return new ResultadoAnalisis(palabras.length, pos, neg, sentimiento);
    }
}
