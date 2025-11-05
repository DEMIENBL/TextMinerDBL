package com.demien.textminer.controller;

import com.demien.textminer.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @GetMapping
    public Map<String, Object> obtenerHistorial() throws IOException {
        return historialService.obtenerHistorial();
    }
}
