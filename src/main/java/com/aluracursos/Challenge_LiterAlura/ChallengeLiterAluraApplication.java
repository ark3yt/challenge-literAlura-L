package com.aluracursos.Challenge_LiterAlura;

import com.aluracursos.Challenge_LiterAlura.principal.Principal;
import com.aluracursos.Challenge_LiterAlura.repository.IAutoresRepository;
import com.aluracursos.Challenge_LiterAlura.repository.ILibrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChallengeLiterAluraApplication implements CommandLineRunner {

	@Autowired
	private IAutoresRepository autoresRepositorio;
	@Autowired
	private ILibrosRepository librosRepositorio;
	public static void main(String[] args) {
		SpringApplication.run(ChallengeLiterAluraApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(autoresRepositorio,librosRepositorio);
		principal.muestraElMenu();

	}
}
