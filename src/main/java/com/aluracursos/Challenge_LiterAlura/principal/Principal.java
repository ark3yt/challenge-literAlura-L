package com.aluracursos.Challenge_LiterAlura.principal;

import com.aluracursos.Challenge_LiterAlura.model.*;
import com.aluracursos.Challenge_LiterAlura.repository.IAutoresRepository;
import com.aluracursos.Challenge_LiterAlura.repository.ILibrosRepository;
import com.aluracursos.Challenge_LiterAlura.service.ConsumoAPI;
import com.aluracursos.Challenge_LiterAlura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in); //aca creamos la instancia de nuestro scanner para leer lo q ingresa el usuario
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private static final String URL_BASE = "https://gutendex.com/books/?search="; // ESTA ES LA CONSTANTE URL BASE
    private IAutoresRepository autoresRepositorio;
    private ILibrosRepository librosRepositorio;

    public Principal(IAutoresRepository autoresRepositorio, ILibrosRepository librosRepositorio) {
        this.autoresRepositorio = autoresRepositorio;
        this.librosRepositorio = librosRepositorio;
    }

    public void muestraElMenu() {
        var opcion = -1;
        System.out.println("Bienvenido! Por favor selecciona una opción: ");
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idiomas
                    6 - Top 10 mejores libros
                    7 - Estadisticas
                    8 - Recomendaciones sitios web para comprar libros
                    0 - Salir
                    """;
            System.out.println(menu);
            // Aseguramos que el valor ingresado sea un número
            while (!teclado.hasNextInt()) {
                System.out.println("¡Error! Por favor ingresa un número válido.");
                teclado.nextLine();  // Limpiamos el buffer
            }
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibrosPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresPorAño();
                    break;
                case 5:
                    listarLibrosPorIdiomas();
                    break;
                case 6:
                    top10MejoresLibros();
                    break;
                case 7:
                    estadisticas();
                    break;
                case 8:
                    recomendarWebCompraLibros();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }
    private Datos getDatosLibros() {
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + tituloLibro.replace(" ", "+"));
        Datos datosLibros = conversor.obtenerDatos(json, Datos.class);
        return datosLibros;
    }
    private Libros crearLibro(DatosLibros datosLibros, Autores autor) {
        if (autor != null) {
            return new Libros(datosLibros, autor);
        } else {
            System.out.println("El autor es null, no se puede crear el libro");
            return null;
        }
    }
    private void buscarLibrosPorTitulo() {
        System.out.println("Ingrese el nombre del libro que desea buscar");
        Datos datos = getDatosLibros();
        if (!datos.resultados().isEmpty()) {
            DatosLibros datosLibros = datos.resultados().get(0);
            DatosAutores datosAutores = datosLibros.autor().get(0);
            Libros libro = null;
            Libros libroRepositorio = librosRepositorio.findByTitulo(datosLibros.titulo()); //finByTitulo creado en interface Ilibrosrepository
            if (libroRepositorio != null) {
                System.out.println("Este libro ya se encuentra en la base de datos");
                System.out.println(libroRepositorio.toString());
            } else {
                Autores autorRepositorio = autoresRepositorio.findByNameIgnoreCase(datosLibros.autor().get(0).nombre());
                if (autorRepositorio != null) {
                    libro = crearLibro(datosLibros, autorRepositorio);
                    librosRepositorio.save(libro);
                    System.out.println("----- LIBRO AGREGADO -----\n");
                    System.out.println(libro);
                } else {
                    Autores autor = new Autores(datosAutores);
                    autor = autoresRepositorio.save(autor);
                    libro = crearLibro(datosLibros, autor);
                    librosRepositorio.save(libro);
                    System.out.println("----- LIBRO AGREGADO -----\n");
                    System.out.println(libro);
                }
            }
        } else {
            System.out.println("El libro no existe en la API de Gutendex, ingresa otro");
        }
    }
    private void listarLibrosRegistrados() {
        List<Libros> libros = librosRepositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados");
            return;
        }
        System.out.println("----- LOS LIBROS REGISTRADOS SON: -----\n");
        libros.stream()
                .sorted(Comparator.comparing(Libros::getTitulo))
                .forEach(System.out::println);
    }
    private void listarAutoresRegistrados() {
        List<Autores> autores = autoresRepositorio.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados");
            return;
        }
        System.out.println("----- LOS AUTORES REGISTRADOS SON: -----\n");
        autores.stream()
                .sorted(Comparator.comparing(Autores::getName))
                .forEach(System.out::println);
    }
    private void listarAutoresPorAño() {
        System.out.println("Escribe el año en el que deseas buscar: ");
        var año = teclado.nextInt();
        teclado.nextLine();
        if(año < 0) {
            System.out.println("El año no puede ser negativo, intenta de nuevo");
            return;
        }
        List<Autores> autoresPorAño = autoresRepositorio.findByFechaDeNacimientoLessThanEqualAndFechaDeFallecimientoGreaterThanEqual(año, año);
        if (autoresPorAño.isEmpty()) {
            System.out.println("No hay autores registrados en ese año");
            return;
        }
        System.out.println("----- LOS AUTORES VIVOS REGISTRADOS EN EL AÑO " + año + " SON: -----\n");
        autoresPorAño.stream()
                .sorted(Comparator.comparing(Autores::getName))
                .forEach(System.out::println);
    }
    private void listarLibrosPorIdiomas() {
        System.out.println("Escribe el idioma por el que deseas buscar: ");
        String menu = """
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués
                """;
        System.out.println(menu);
        var idioma = teclado.nextLine();
        if (!idioma.equals("es") && !idioma.equals("en") && !idioma.equals("fr") && !idioma.equals("pt")) {
            System.out.println("Idioma no válido, intenta de nuevo");
            return;
        }
        List<Libros> librosPorIdioma = librosRepositorio.findByIdiomasContaining(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No hay libros registrados en ese idioma");
            return;
        }
        System.out.println("----- LOS LIBROS REGISTRADOS EN EL IDIOMA SELECCIONADO SON: -----\n");
        librosPorIdioma.stream()
                .sorted(Comparator.comparing(Libros::getTitulo))
                .forEach(System.out::println);
    }
    private void top10MejoresLibros() {
        System.out.println("De donde quieres obtener los libros más descargados?");
        String menu = """
                1 - Gutendex
                2 - Base de datos
                """;
        System.out.println(menu);
        var opcion = teclado.nextInt();
        teclado.nextLine();

        if (opcion == 1) {
            System.out.println("----- LOS 10 LIBROS MÁS DESCARGADOS EN GUTENDEX SON: -----\n");
            var json = consumoAPI.obtenerDatos(URL_BASE);
            Datos datos = conversor.obtenerDatos(json, Datos.class);
            List<Libros> libros = new ArrayList<>();
            for (DatosLibros datosLibros : datos.resultados()) {

                if (!datosLibros.autor().isEmpty()) {
                    Autores autor = new Autores(datosLibros.autor().get(0));
                    Libros libro = new Libros(datosLibros, autor);
                    libros.add(libro);
                } else {
                    System.out.println("No se encontró autor para el libro: " + datosLibros.titulo());
                }
            }
            libros.stream()
                    .sorted(Comparator.comparing(Libros::getNumeroDeDescargas).reversed())
                    .limit(10)
                    .forEach(System.out::println);
        } else if (opcion == 2) {
            System.out.println("----- LOS 10 LIBROS MÁS DESCARGADOS EN LA BASE DE DATOS SON: -----\n");
            List<Libros> libros = librosRepositorio.findAll();
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados");
                return;
            }
            libros.stream()
                    .sorted(Comparator.comparing(Libros::getNumeroDeDescargas).reversed())
                    .limit(10)
                    .forEach(System.out::println);
        } else {
            System.out.println("Opción no válida, intenta de nuevo");
        }
    }
    private void estadisticas() {
        System.out.println("De donde quieres obtener las estadísiticas: ");
        String menu = """
                1 - Gutendex
                2 - Base de datos
                """;
        System.out.println(menu);

        int opcion = -1;
        boolean entradaValida = false;

        // Bucle para seguir pidiendo la entrada hasta que sea válida
        while (!entradaValida) {
            try {
                opcion = teclado.nextInt();  // Intentamos leer un número
                teclado.nextLine();
                if (opcion == 1 || opcion == 2) {
                    entradaValida = true;  // La entrada es válida, rompemos el bucle
                } else {
                    System.out.println("¡Error! Por favor ingresa un número válido (1 o 2).");
                }
            } catch (InputMismatchException e) {
                // Capturamos el caso cuando el usuario ingresa algo que no es un número
                System.out.println("¡Error! Por favor ingresa un número válido (1 o 2).");
                teclado.nextLine();  // Limpiamos el buffer para evitar un bucle infinito
            }
        }

        if (opcion == 1) {
            System.out.println("----- ESTADÍSTICAS DE DESCARGAS EN GUTENDEX -----\n");
            var json = consumoAPI.obtenerDatos(URL_BASE);
            Datos datos = conversor.obtenerDatos(json, Datos.class);
            DoubleSummaryStatistics estadisticas = datos.resultados().stream()
                    .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
            System.out.println("Libro con más descargas: " + estadisticas.getMax());
            System.out.println("Libro con menos descargas: " + estadisticas.getMin());
            System.out.println("Promedio de descargas: " + estadisticas.getAverage());
            System.out.println("\n");
        } else if (opcion == 2) {
            System.out.println("----- ESTADÍSTICAS DE DESCARGAS EN LA BASE DE DATOS -----\n");
            List<Libros> libros = librosRepositorio.findAll();
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados");
                return;
            }
            DoubleSummaryStatistics estadisticas = libros.stream()
                    .collect(Collectors.summarizingDouble(Libros::getNumeroDeDescargas));
            System.out.println("Libro con más descargas: " + estadisticas.getMax());
            System.out.println("Libro con menos descargas: " + estadisticas.getMin());
            System.out.println("Promedio de descargas: " + estadisticas.getAverage());
            System.out.println("\n");
        } else {
            System.out.println("Opción no válida, intenta de nuevo");
        }
    }
    private void recomendarWebCompraLibros() {
        System.out.println("----- RECOMENDACIONES DE PÁGINAS WEB PARA COMPRAR LIBROS -----");
        System.out.println("A continuación, algunas páginas web donde puedes comprar libros:");
        System.out.println("1. Amazon - https://www.amazon.com");
        System.out.println("2. Barnes & Noble - https://www.barnesandnoble.com");
        System.out.println("3. Book Depository - https://www.bookdepository.com");
        System.out.println("4. Casa del Libro - https://www.casadellibro.com");
        System.out.println("5. Librería Gandhi - https://www.gandhi.com.mx");
        System.out.println("6. El Corte Inglés (España) - https://www.elcorteingles.es/libros/");
        System.out.println("7. MercadoLibre (Latinoamérica) - https://www.mercadolibre.com");
        System.out.println("¡Explora estos sitios y encuentra tu próximo libro!");
        System.out.println("\n");
    }
}