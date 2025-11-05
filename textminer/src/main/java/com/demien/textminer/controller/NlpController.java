package com.demien.textminer.controller;

import com.demien.textminer.service.NlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nlp")
public class NlpController {

    @Autowired
    private NlpService nlpService;

    @PostMapping
    public Map<String, Object> procesar(@RequestBody String texto) throws Exception {
        return nlpService.procesarTexto(texto);
    }
}
