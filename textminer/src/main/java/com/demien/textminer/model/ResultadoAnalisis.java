package com.demien.textminer.model;

import java.util.List;

public class ResultadoAnalisis {
    private int totalPalabras;
    private List<String> positivas;
    private List<String> negativas;
    private String sentimiento;

    // Constructor
    public ResultadoAnalisis(int totalPalabras, List<String> positivas, List<String> negativas, String sentimiento) {
        this.totalPalabras = totalPalabras;
        this.positivas = positivas;
        this.negativas = negativas;
        this.sentimiento = sentimiento;
    }

    // Getters y setters
    public int getTotalPalabras() { return totalPalabras; }
    public List<String> getPositivas() { return positivas; }
    public List<String> getNegativas() { return negativas; }
    public String getSentimiento() { return sentimiento; }
}
