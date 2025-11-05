package com.demien.textminer.controller;

import com.demien.textminer.service.ResumenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/resumen")
public class ResumenController {

    @Autowired
    private ResumenService resumenService;

    @PostMapping
    public Map<String, Object> resumir(@RequestBody String texto) throws Exception {
        return resumenService.generarResumen(texto);
    }
}
