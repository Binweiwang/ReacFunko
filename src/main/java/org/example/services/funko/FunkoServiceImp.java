package org.example.services.funko;

import org.example.model.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class FunkoServiceImp implements FunkoService {
    @Override
    public Flux<Funko> findAll() {
        return null;
    }

    @Override
    public Flux<Funko> findAllByNombre(String nombre) {
        return null;
    }

    @Override
    public Mono<Funko> findById(long id) {
        return null;
    }

    @Override
    public Mono<Funko> findByUuid(UUID uuid) {
        return null;
    }

    @Override
    public Mono<Funko> save(Funko alumno) {
        return null;
    }

    @Override
    public Mono<Funko> update(Funko alumno) {
        return null;
    }

    @Override
    public Mono<Funko> deleteById(long id) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }

    @Override
    public Mono<Funko> importarCSV() {
        return null;
    }

    @Override
    public Mono<Void> exportarJSON() {
        return null;
    }
}
