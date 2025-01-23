package com.aluracursos.Challenge_LiterAlura.service;

public interface IconvierteDatos {
    <T> T obtenerDatos(String json, Class <T> clase);
}
