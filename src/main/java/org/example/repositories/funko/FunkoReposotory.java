package org.example.repositories.funko;

import org.example.model.Funko;
import org.example.repositories.crud.CrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FunkoReposotory extends CrudRepository<Funko,Long> {
    Flux<Funko> findByNombre(String nombre);
    Mono<Funko> findByUuid(UUID uuid);
}
