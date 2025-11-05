package com.demien.textminer.controller;

import com.demien.textminer.model.ResultadoAnalisis;
import com.demien.textminer.service.AnalisisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analizar")
public class AnalisisController {

    @Autowired
    private AnalisisService analisisService;

    @PostMapping
    public ResultadoAnalisis analizar(@RequestBody String texto) {
        return analisisService.analizarTexto(texto);
    }
}
