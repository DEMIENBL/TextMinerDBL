package com.demien.textminer.controller;

import com.demien.textminer.service.LdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/temas")
public class LdaController {

    @Autowired
    private LdaService ldaService;

    @PostMapping
    public Map<String, Object> analizarTemas(@RequestBody String texto) throws Exception {
        return ldaService.analizarTemas(texto);
    }
}
