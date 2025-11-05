package com.demien.textminer.controller;

import com.demien.textminer.service.AnalisisCompletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analisis-completo")
public class AnalisisCompletoController {

    @Autowired
    private AnalisisCompletoService analisisCompletoService;

    @PostMapping
    public Map<String, Object> analizarTodo(@RequestBody String texto) throws Exception {
        return analisisCompletoService.analizarTodo(texto);
    }
}
