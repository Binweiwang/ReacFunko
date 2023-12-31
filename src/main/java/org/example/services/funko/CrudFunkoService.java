package org.example.services.funko;

import org.example.model.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudFunkoService {
    // Metodos CRUD
    Flux<Funko> findAll();

    Flux<Funko> findAllByNombre(String nombre);

    Mono<Funko> findById(long id);

    Mono<Funko> save(Funko alumno);

    Mono<Funko> update(Funko alumno);

    Mono<Funko> deleteById(long id);

    Mono<Void> deleteAll();
}
