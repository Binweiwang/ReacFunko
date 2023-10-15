package org.example.services.funko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.exceptions.FunkoNoEncontradoException;
import org.example.model.Funko;
import org.example.model.Notificacion;
import org.example.model.Tipo;
import org.example.repositories.funko.FunkoRepository;
import org.example.repositories.funko.FunkoRepositoryImp;
import org.example.services.cache.FunkoCache;
import org.example.services.cache.FunkoCacheImp;
import org.example.services.notificacion.FunkosNotificacion;
import org.example.services.notificacion.NotificacionFunkoImp;
import org.example.util.IdGenerator;
import org.example.util.LocalDateAdapter;
import org.example.util.LocalDateTimeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FunkoServiceImp implements FunkoService {
    // Atributos
    private static FunkoServiceImp instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImp.class);
    private final FunkoRepository funkoRepository;
    private final FunkosNotificacion notificacionFunko;
    private final FunkoCache cache;
    private final int MAX_SIZE = 15;

    // Constructor privado
    private FunkoServiceImp(FunkoRepository funkoReposotory) {
        this.funkoRepository = funkoReposotory;
        this.notificacionFunko = NotificacionFunkoImp.getInstance();
        this.cache = new FunkoCacheImp(MAX_SIZE);

    }

    /**
     * Singleton
     *
     * @param funkoReposotory repositorio
     * @return instancia
     */

    public static FunkoServiceImp getInstance(FunkoRepository funkoReposotory) {
        if (instance == null) {
            instance = new FunkoServiceImp(funkoReposotory);
        }
        return instance;
    }

    /**
     * Devuelve todos los funkos
     *
     * @return funkos
     */
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Mostrando todos los funkos");
        return funkoRepository.findAll();
    }

    /**
     * Devuelve todos los funkos por nombre
     *
     * @param nombre nombre
     * @return funkos
     */
    @Override
    public Flux<Funko> findAllByNombre(String nombre) {
        logger.debug("Mostrando funko por nombre: " + nombre);
        return funkoRepository.findByNombre(nombre);
    }

    /**
     * Devuelve un funko por id
     *
     * @param id id
     * @return funko
     */
    @Override
    public Mono<Funko> findById(long id) {
        logger.debug("Mostrando funko por id: " + id);
        return cache.get(id)
                .switchIfEmpty(funkoRepository.findById(id)
                        .flatMap(funko -> cache.put(funko.getId(), funko)
                                .then(Mono.just(funko))))
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con id " + id + " no encontrado")));
    }

    /**
     * Guarda un funko
     *
     * @param funko funko
     * @return funko guardado
     */
    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando funko: " + funko);
//        return funkorepositorio.save(funko)
//                .flatMap(saved -> findByUuid(saved.getCod()))
//                .doOnSuccess(saved -> notificacionFunko.notify(new Notificacion<>(Tipo.NEW, saved)));

        var res = funkoRepository.save(funko)
                .flatMap(saved -> findByUuid(saved.getCod()));
        res.subscribe(saved -> notificacionFunko.notify(new Notificacion<>(Tipo.NEW, saved)));
        return res;

    }

    /**
     * Actualiza un funko
     *
     * @param funko funko
     * @return funko actualizado
     */
    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
//        return funkorepositorio.findById(funko.getId())
//                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Alumno con id " + funko.getId() + " no encontrado")))
//                .flatMap(existing -> funkorepositorio.update(funko)
//                        .flatMap(updated -> cache.put(updated.getId(), updated)
//                                .thenReturn(updated))
//                        .doOnSuccess(updated -> notificacionFunko.notify(new Notificacion<>(Tipo.UPDATED, updated))));
        var res = funkoRepository.findById(funko.getId())
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con id " + funko.getId() + " no encontrado")))
                .flatMap(funkoRepository::update)
                .flatMap(updated -> cache.put(updated.getId(), updated)
                        .thenReturn(updated));
        res.subscribe(updated -> notificacionFunko.notify(new Notificacion<>(Tipo.UPDATED, updated)));
        return res;
    }

    /**
     * Borra un funko por id
     *
     * @param id id
     * @return funko borrado
     */
    @Override
    public Mono<Funko> deleteById(long id) {
        logger.debug("Borrando funko por id: " + id);
//        return funkoRepository.findById(id)
//                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con id " + id + " no encontrado")))
//                .flatMap(funko -> cache.remove(funko.getId())
//                        .then(funkoRepository.deleteById(funko.getId()))
//                        .thenReturn(funko))
//                .doOnSuccess(funko -> notificacionFunko.notify(new Notificacion<>(Tipo.DELETED, funko)));
        var res = funkoRepository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con id " + id + " no encontrado")))
                .flatMap(funko -> cache.remove(funko.getId())
                        .then(funkoRepository.deleteById(funko.getId()))
                        .thenReturn(funko));
        res.subscribe(funko -> notificacionFunko.notify(new Notificacion<>(Tipo.DELETED, funko)));
        return res;
    }

    /**
     * Borra todos los funkos
     *
     * @return void
     */
    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Borrando todo los funkos");
        return funkoRepository.deleteAll();
    }

    /**
     * Importa funkos desde un archivo CSV
     *
     * @return funkos importados
     */
    @Override
    public Flux<Funko> importarCSV() {
        logger.debug("Importado funkos desde un archivo");
        var fileCSV = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "funkos.csv";
        List<Funko> funkosToSave = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileCSV))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lines = line.split(",");
                Funko funko = Funko.builder()
                        .myId(IdGenerator.getInstance().getMyId().block())
                        .cod(UUID.fromString(lines[0].substring(1, 35)))
                        .nombre(lines[1])
                        .modelo(lines[2])
                        .precio(Double.parseDouble(lines[3]))
                        .fechaLanzamiento(LocalDate.parse(lines[4]))
                        .build();
                funkosToSave.add(funko);
            }

        } catch (IOException e) {
            return Flux.error(e);
        }

        return Flux.fromIterable(funkosToSave)
                .flatMap(funkoRepository::save)
                .doOnNext(funko -> notificacionFunko.notify(new Notificacion<>(Tipo.NEW, funko)));
    }


    /**
     * Exporta funkos a un archivo CSV
     *
     * @return void
     */
    @Override
    public Mono<Void> exportarJSON() throws IOException {
        logger.debug("Exportando funkos a un archivo");
        String fileJSON = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "funkos.json";
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        List<Funko> funkoList = importarCSV().collectList().block();
        gson.toJson(funkoList, new FileWriter(fileJSON));
        return Mono.empty();
    }

    /**
     * Devuelve un funko por uuid
     *
     * @param uuid uuid
     * @return funko buscado por uuid
     */
    @Override
    public Mono<Funko> findByUuid(UUID uuid) {
        logger.debug("Mostrando funko por uuid: " + uuid);
        return funkoRepository.findByUuid(uuid)
                .flatMap(funko -> cache.put(funko.getMyId(), funko)
                        .then(Mono.just(funko)))
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con uuid " + uuid + " no encontrado")));
    }

    /**
     * Devuelve el funko mas caro
     *
     * @return funko mas caro
     */
    @Override
    public Mono<Funko> funkoMasCaro() {
        logger.debug("Mostrando funko mas caro");
        return funkoRepository.findAll().collectList().flatMap(funkos -> {
            Optional<Funko> funko = funkos.stream().max(Comparator.comparing(Funko::getPrecio));
            return Mono.just(funko.get());
        });
    }

    /**
     * Devuelve la media de los funkos
     *
     * @return media de los funkos
     */
    @Override
    public Mono<Double> mediaFunkos() {
        logger.debug("Mostrando media de los funkos");
        return funkoRepository.findAll().collectList().flatMap(funkos -> {
            Double media = funkos.stream().mapToDouble(Funko::getPrecio).average().getAsDouble();
            return Mono.just(media);
        });

    }

    /**
     * Devuelve los funkos agrupados por modelo
     *
     * @return funkos agrupados por modelo
     */
    @Override
    public Mono<Map<String, List<Funko>>> funkoPorModelo() {
        logger.debug("Mostrando funkos por modelo");
        return funkoRepository.findAll().collectList().flatMap(funkos -> {
            Map<String, List<Funko>> funkosPorModelo = funkos.stream()
                    .collect(Collectors.groupingBy(Funko::getModelo));
            return Mono.just(funkosPorModelo);
        });
    }


    /**
     * Devuelve el numero de funkos por modelo
     *
     * @return numero de funkos por modelo
     */
    @Override
    public Mono<Map<String, Integer>> numeroFunkosPorModelo() {
        logger.debug("Mostrando numero de funkos por modelo");
        return funkoRepository.findAll().collectList()
                .flatMap(funkos -> {
                    Map<String, Integer> numeroFunkosPorModelo = funkos.stream()
                            .map(Funko::getModelo)
                            .collect(HashMap::new, (map, modelo) -> map.merge(modelo, 1, Integer::sum), HashMap::putAll);
                    return Mono.just(numeroFunkosPorModelo);
                });
    }

    /**
     * Devuelve los funkos lanzados en 2023
     *
     * @return funkos lanzados en 2023
     */
    @Override
    public Mono<List<Funko>> funkosLanzadosEn2023() {
        logger.debug("Mostrando funkos lanzados en 2023");
        return funkoRepository.findAll().collectList().flatMap(funkos -> {
            List<Funko> funkosLanzadosen2023 = funkos.stream()
                    .filter(funko -> funko.getFechaLanzamiento().getYear() == 2023)
                    .toList();
            return Mono.just(funkosLanzadosen2023);
        });
    }

    /**
     * Devuelve el numero de funkos de stitch
     *
     * @return numero de funkos de stitch
     */
    @Override
    public Mono<Integer> numeroFunkosStitch() {
        logger.debug("Mostrando numero de funkos de stitch");
        return funkoRepository.findAll().collectList().flatMap(funkos -> {
            Integer numeroFunkosStitch = (int) funkos.stream()
                    .filter(funko -> funko.getNombre().contains("Stitch"))
                    .count();
            return Mono.just(numeroFunkosStitch);
        });
    }

    /**
     * Devuelve los funkos de stitch
     *
     * @return funkos de stitch
     */
    @Override
    public Mono<List<Funko>> funkosStitch() {
        logger.debug("Mostrando funkos de stitch");
        return funkoRepository.findAll().collectList().flatMap(funkos -> {
            List<Funko> funkosStitch = funkos.stream()
                    .filter(funko -> funko.getNombre().contains("Stitch"))
                    .toList();
            return Mono.just(funkosStitch);
        });
    }
}
