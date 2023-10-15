package org.example.repositories.crud;

import org.example.model.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudRepository<T,ID> {
    // Metodos CRUD
    Flux<Funko> findAll();
    Mono<Funko> findById(ID id);
    Mono<Funko> save(T t);
    Mono<Funko> update(T t);
    Mono<Boolean> deleteById(ID id);
    Mono<Void> deleteAll();

}
