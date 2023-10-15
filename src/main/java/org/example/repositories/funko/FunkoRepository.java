package org.example.repositories.funko;

import org.example.model.Funko;
import org.example.repositories.crud.CrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FunkoRepository extends CrudRepository<Funko,Long> {
    // Metodos propios
    Flux<Funko> findByNombre(String nombre);
    Mono<Funko> findByUuid(UUID uuid);
}
