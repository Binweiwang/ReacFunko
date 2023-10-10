package org.example.repositories.funko;

import io.r2dbc.pool.ConnectionPool;
import org.example.database.DatabaseManager;
import org.example.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FunkoReposotoryImpl implements funkoReposotory {
    private static funkoReposotory instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoReposotoryImpl.class);
    private final ConnectionPool connectionFactory;

    private FunkoReposotoryImpl(DatabaseManager databaseManager){
        this.connectionFactory = databaseManager.getConnectionPoll();
    }
    public static FunkoReposotoryImpl getInstance(DatabaseManager databaseManager){
        if(instance == null){
            instance = new FunkoReposotoryImpl(databaseManager);
        }
        return instance;
    }
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todo los alumnos");
        String sql = "SELECT * FROM FUNKOS";
        return ;
    }

    @Override
    public Mono<Funko> findById(Object o) {
        return null;
    }

    @Override
    public Mono<Funko> save(Object o) {
        return null;
    }

    @Override
    public Mono<Funko> update(Object o) {
        return null;
    }

    @Override
    public Mono<Boolean> deleteById(Object o) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }

    @Override
    public Mono<Funko> findByNombre() {
        return null;
    }
}
