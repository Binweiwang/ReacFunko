package org.example.services.funko;

import org.example.model.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FunkoService {
    Flux<Funko> findAll();

    Flux<Alumno> findAllByNombre(String nombre);

    Mono<Alumno> findById(long id);

    Mono<Alumno> findByUuid(UUID uuid);

    Mono<Alumno> save(Alumno alumno);

    Mono<Alumno> update(Alumno alumno);

    Mono<Alumno> deleteById(long id);

    Mono<Void> deleteAll();
}
