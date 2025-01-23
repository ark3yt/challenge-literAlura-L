package com.aluracursos.Challenge_LiterAlura.repository;

import com.aluracursos.Challenge_LiterAlura.model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ILibrosRepository extends JpaRepository<Libros, Long> {
    Libros findByTitulo (String titulo); //finBy encontrar por el atributo, en este caso titulo titulo

    List<Libros> findByIdiomasContaining(String idiomas);
}
