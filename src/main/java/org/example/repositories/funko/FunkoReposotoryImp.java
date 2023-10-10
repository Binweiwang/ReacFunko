package org.example.repositories.funko;

import io.r2dbc.pool.ConnectionPool;
import org.example.database.DatabaseManager;
import org.example.model.Funko;
import org.example.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.r2dbc.spi.Connection;

import java.time.LocalDateTime;
import java.util.UUID;

public class FunkoReposotoryImp implements FunkoReposotory {
    private static FunkoReposotoryImp instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoReposotoryImp.class);
    private final ConnectionPool connectionFactory;

    private FunkoReposotoryImp(DatabaseManager databaseManager) {
        this.connectionFactory = databaseManager.getConnectionPoll();
    }

    public static FunkoReposotoryImp getInstance(DatabaseManager databaseManager) {
        if (instance == null) {
            instance = new FunkoReposotoryImp(databaseManager);
        }
        return instance;
    }

    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todo los alumnos");
        String sql = "SELECT * FROM FUNKOS";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(sql).execute())
                        .flatMap(result -> result.map((row, rowMetadata) ->
                                Funko.builder()
                                        .id(row.get("id", Long.class))
                                        .nombre(row.get("nombre", String.class))
                                        .cod(row.get("uuid", UUID.class))
                                        .myId(row.get("myId", Long.class))
                                        .created_at(row.get("created_at", LocalDateTime.class))
                                        .updated_at(row.get("updated_at", LocalDateTime.class))
                                        .build()
                        )),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> findById(Long id) {
        logger.debug("Buscando funko por id: " + id);
        String sql = "SELECT * FROM FUNKOS WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, id)
                        .execute()
                ).flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                        Funko.builder()
                                .id(row.get("id", Long.class))
                                .nombre(row.get("nombre", String.class))
                                .cod(row.get("uuid", UUID.class))
                                .myId(row.get("myId", Long.class))
                                .created_at(row.get("created_at", LocalDateTime.class))
                                .updated_at(row.get("updated_at", LocalDateTime.class))
                                .build()))),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando funko: " + funko);
        String sql = "INSERT INTO FUNKOS (cod, myId, nombre, modelo, precio, fecha_lanzamiento, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?)";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, funko.getCod())
                        .bind(1, IdGenerator.getInstance().getMyId())
                        .bind(2, funko.getNombre())
                        .bind(3, funko.getModelo())
                        .bind(4, funko.getPrecio())
                        .bind(5, funko.getFechaLanzamiento())
                        .bind(6, funko.getCreated_at())
                        .bind(7, funko.getUpdated_at())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
        String sql = "UPDATE FUNKOS SET nombre = ?, modelo = ?, precio = ?, updated_at = ? WHERE id = ?";
        funko.setUpdated_at(LocalDateTime.now());
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> (Mono.from(connection.createStatement(sql)
                        .bind(0, funko.getNombre())
                        .bind(1, funko.getModelo())
                        .bind(2, funko.getPrecio())
                        .bind(3, funko.getUpdated_at()).execute())).then(Mono.just(funko)),
                Connection::close
        );
    }

    @Override
    public Mono<Boolean> deleteById(Long id) {
        logger.debug("Eliminar por la id: " + id);
        String sql = "DELETE FROM FUNKOS WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, id)
                        .execute()).flatMapMany(result -> result.getRowsUpdated())
                        .hasElements(),
                Connection::close
        );
    }

    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Elimina todos los funkos");
        String sql = "DELETE FROM FUNKOS";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> (Mono.from(connection.createStatement(sql).execute()))
                        .then(),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> findByNombre(String nombre) {
        logger.debug("Busca por nombre: " + nombre);
        String sql = "SELECT * FROM FUNKOS WHERE nombre = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> (Mono.from(connection.createStatement(sql)
                        .bind(0,nombre)
                        .execute()))
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                        Funko.builder()
                                .id(row.get("id", Long.class))
                                .nombre(row.get("nombre", String.class))
                                .cod(row.get("uuid", UUID.class))
                                .myId(row.get("myId", Long.class))
                                .created_at(row.get("created_at", LocalDateTime.class))
                                .updated_at(row.get("updated_at", LocalDateTime.class))
                                .build()))),
                Connection::close
        );
    }
}
