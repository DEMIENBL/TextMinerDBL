package com.demien.textminer.controller;

import com.demien.textminer.service.ResumenTematicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/resumen-tematico")
public class ResumenTematicoController {

    @Autowired
    private ResumenTematicoService resumenTematicoService;

    @PostMapping
    public Map<String, Object> generarResumenTematico(@RequestBody String texto) throws Exception {
        return resumenTematicoService.generarResumenTematico(texto);
    }
}
