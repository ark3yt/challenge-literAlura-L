package com.aluracursos.Challenge_LiterAlura.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;
@Entity // con Entity estamos indicando a JPA que esto es una tabla para nuestra base de datos
@Table(name = "libros") // libros seria el nombre de nuestra tabla
public class Libros {
    @Id // Id es el identificador de nuestra tabla libros
    @GeneratedValue(strategy = GenerationType.IDENTITY)
            private long id;
            public String titulo;

            @ManyToOne
            @JoinColumn(name = "autor_id", nullable = false)
            private Autores autor;

            @Column(name = "nombre_autor")
            private String nombreAutor;

            @Column(name = "idiomas")
            private String idiomas;
            private double numeroDeDescargas;

            public Libros() {}

    public Libros(DatosLibros datosLibros, Autores autor) {
        this.titulo = datosLibros.titulo();
        setIdiomas(datosLibros.idiomas());
        this.numeroDeDescargas = datosLibros.numeroDeDescargas();
        this.nombreAutor = autor.getName();
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autores getAutor() {
        return autor;
    }

    public void setAutor(Autores autor) {
        this.autor = autor;
    }

    public List<String> getIdiomas() {
        return Arrays.asList(idiomas.split(","));
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = String.join(",", idiomas);
    }

    public double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }
    @Override
    public String toString() {
        return "--------------- LIBRO ---------------" + "\n" +
                "Título: " + titulo + "\n" +
                "Autor: " + nombreAutor + "\n" +
                "Idioma: " + idiomas + "\n" +
                "Número de descargas: " + numeroDeDescargas + "\n" +
                "------------------------------------" + "\n";
    }
}
