package org.example.repositories.funko;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Row;
import org.example.database.DatabaseManager;
import org.example.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.r2dbc.spi.Connection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class FunkoRepositoryImp implements FunkoRepository {
    // Atributos
    private static FunkoRepositoryImp instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImp.class);
    private final ConnectionPool connectionFactory;

    // Constructor privado
    private FunkoRepositoryImp(DatabaseManager databaseManager) {
        this.connectionFactory = databaseManager.getConnectionPoll();
    }

    // Singleton
    public static FunkoRepositoryImp getInstance(DatabaseManager databaseManager) {
        if (instance == null) {
            instance = new FunkoRepositoryImp(databaseManager);
        }
        return instance;
    }

    /**
     * Busca todos los funkos
     *
     * @return funkos encontrados
     */
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todo los Funkos");
        String sql = "SELECT * FROM FUNKOS";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(sql).execute())
                        .flatMap(result -> result.map((row, rowMetadata) ->
                                getBuild(row)
                        )),
                Connection::close
        );
    }

    /**
     * Construye un funko
     *
     * @param row fila
     * @return funko construido
     */
    private static Funko getBuild(Row row) {
        return Funko.builder()
                .id(row.get("id", Integer.class))
                .cod(row.get("cod", UUID.class))
                .nombre(row.get("nombre", String.class))
                .myId(row.get("myId", Long.class))
                .modelo(row.get("modelo", String.class))
                .precio(row.get("precio", Double.class))
                .fechaLanzamiento(row.get("fecha_lanzamiento", LocalDate.class))
                .created_at(row.get("created_at", LocalDateTime.class))
                .updated_at(row.get("updated_at", LocalDateTime.class))
                .build();
    }

    /**
     * Busca un funko por id
     *
     * @param id
     * @return funko encontrado por id
     */
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
                        getBuild(row)
                ))),
                Connection::close
        );
    }

    /**
     * Guarda un funko
     *
     * @param funko se va a guardar
     * @return funko guardado
     */
    @Override
    public Mono<Funko> save(Funko funko) {
        String sql = "INSERT INTO FUNKOS (cod, myId, nombre, modelo, precio, fecha_lanzamiento, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?)";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, funko.getCod())
                        .bind(1, funko.getMyId())
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

    /**
     * Actualiza un funko
     *
     * @param funko se va a actualizar
     * @return funko actualizado
     */
    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
        String sql = "UPDATE FUNKOS SET nombre = ?, modelo = ?, precio = ?, updated_at = ? WHERE id = ?";
        funko.setUpdated_at(LocalDateTime.now());
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, funko.getNombre())
                        .bind(1, funko.getModelo())
                        .bind(2, funko.getPrecio())
                        .bind(3, funko.getUpdated_at())
                        .bind(4, funko.getId())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    /**
     * Elimina un funko por id
     *
     * @param id de funko a eliminar
     * @return true si se ha eliminado
     */
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

    /**
     * Elimina todos los funkos
     *
     * @return void
     */
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

    /**
     * Busca por nombre
     *
     * @param nombre de funko a buscar
     * @return funkos encontrados por nombre
     */
    @Override
    public Flux<Funko> findByNombre(String nombre) {
        logger.debug("Busca por nombre: " + nombre);
        String sql = "SELECT * FROM FUNKOS WHERE nombre LIKE ?";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> (Flux.from(connection.createStatement(sql)
                        .bind(0, nombre)
                        .execute()))
                        .flatMap(result -> Flux.from(result.map((row, rowMetadata) ->
                                getBuild(row))
                        )),
                Connection::close
        );
    }

    /**
     * Busca por UUID
     *
     * @param uuid de funko a buscar
     * @return funkos encontrados por UUID
     */
    @Override
    public Mono<Funko> findByUuid(UUID uuid) {
        String sql = "SELECT * FROM FUNKOS WHERE cod = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                                .bind(0, uuid)
                                .execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                                Funko.builder()
                                        .id(row.get("id", Integer.class))
                                        .nombre(row.get("nombre", String.class))
                                        .cod(row.get("cod", UUID.class))
                                        .myId(row.get("myId", Long.class))
                                        .created_at(row.get("created_at", LocalDateTime.class))
                                        .updated_at(row.get("updated_at", LocalDateTime.class))
                                        .build()))),
                Connection::close
        );
    }
}
