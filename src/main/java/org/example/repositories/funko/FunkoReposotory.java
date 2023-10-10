package org.example.repositories.funko;

import org.example.model.Funko;
import org.example.repositories.crud.CrudRepository;
import reactor.core.publisher.Mono;

public interface FunkoReposotory extends CrudRepository<Funko,Long> {
    Mono<Funko> findByNombre(String nombre);
}
