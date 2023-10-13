package org.example.services.funko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.exceptions.FunkoNoEncontradoException;
import org.example.model.Funko;
import org.example.model.Notificacion;
import org.example.model.Tipo;
import org.example.repositories.funko.FunkoReposotory;
import org.example.repositories.funko.FunkoReposotoryImp;
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

public class FunkoServiceImp implements FunkoService {
    private static FunkoServiceImp instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoReposotoryImp.class);
    private final FunkoReposotory funkorepositorio;
    private final FunkosNotificacion notificacionFunko;
    private final FunkoCache cache;
    private final int MAX_SIZE = 15;

    private FunkoServiceImp(FunkoReposotory funkoReposotory) {
        this.funkorepositorio = funkoReposotory;
        this.notificacionFunko = NotificacionFunkoImp.getInstance();
        this.cache = new FunkoCacheImp(MAX_SIZE);

    }

    public static FunkoServiceImp getInstance(FunkoReposotory funkoReposotory) {
        if (instance == null) {
            instance = new FunkoServiceImp(funkoReposotory);
        }
        return instance;
    }

    @Override
    public Flux<Funko> findAll() {
        logger.debug("Mostrando todos los funkos");
        return funkorepositorio.findAll();
    }

    @Override
    public Flux<Funko> findAllByNombre(String nombre) {
        logger.debug("Mostrando funko por nombre: " + nombre);
        return funkorepositorio.findByNombre(nombre);
    }

    @Override
    public Mono<Funko> findById(long id) {
        logger.debug("Mostrando funko por id: " + id);
        return cache.get(id)
                .switchIfEmpty(funkorepositorio.findById(id)
                        .flatMap(funko -> cache.put(funko.getId(), funko)
                                .then(Mono.just(funko))))
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con id " + id + " no encontrado")));
    }

    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando funko: " + funko);
        return funkorepositorio.save(funko)
                .flatMap(saved -> findByUuid(saved.getCod()))
                .doOnSuccess(saved -> notificacionFunko.notify(new Notificacion<>(Tipo.NEW, saved)));

    }

    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
        return funkorepositorio.findById(funko.getId())
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Alumno con id " + funko.getId() + " no encontrado")))
                .flatMap(existing -> funkorepositorio.update(funko)
                        .flatMap(updated -> cache.put(updated.getId(), updated)
                                .thenReturn(updated))
                        .doOnSuccess(updated -> notificacionFunko.notify(new Notificacion<>(Tipo.UPDATED, updated))));    }

    @Override
    public Mono<Funko> deleteById(long id) {
        logger.debug("Borrando funko por id: " + id);
        return funkorepositorio.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Alumno con id " + id + " no encontrado")))
                .flatMap(funko -> cache.remove(funko.getId())
                        .then(funkorepositorio.deleteById(funko.getId()))
                        .thenReturn(funko))
                .doOnSuccess(funko -> notificacionFunko.notify(new Notificacion<>(Tipo.DELETED, funko)));
    }

    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Borrando todo los funkos");
        return funkorepositorio.deleteAll();
    }

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
                .flatMap(funkorepositorio::save);
    }


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

    @Override
    public Mono<Funko> findByUuid(UUID uuid) {
        logger.debug("Mostrando funko por uuid: " + uuid);
        return funkorepositorio.findByUuid(uuid)
                .flatMap(funko -> cache.put(funko.getMyId(), funko)
                        .then(Mono.just(funko)))
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("Funko con uuid " + uuid + " no encontrado")));
    }

    @Override
    public Mono<Funko> funkoMasCaro() {
        logger.debug("Mostrando funko mas caro");
        return funkorepositorio.findAll()
                .collectList()
                .flatMap(funkos -> {
                    if (funkos.isEmpty()) {
                        // Manejar el caso en el que la lista de Funkos está vacía.
                        return Mono.error(new FunkoNoEncontradoException("No se encontraron Funkos."));
                    } else {
                        // Encontrar el Funko más caro en la lista.
                        return Mono.just(funkos.stream()
                                .max(Comparator.comparing(Funko::getPrecio)).get());
                    }
                });
    }



    @Override
    public Mono<Double> mediaFunkos() {
        return null;
    }

    @Override
    public Mono<Map<String, Funko>> funkoPorModelo() {
        return null;
    }

    @Override
    public Mono<List<Funko>> funkosLanzadosen2023() {
        return null;
    }

    @Override
    public Mono<Integer> numeroFunkosStitch() {
        return null;
    }

    @Override
    public Mono<List<Funko>> funkosStitch() {
        return null;
    }
}
