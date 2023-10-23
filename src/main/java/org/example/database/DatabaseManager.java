package org.example.database;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.util.stream.Collectors;

public class DatabaseManager {
    // Atributos
    private static DatabaseManager instance;
    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final ConnectionFactory connectionFactory;
    private final ConnectionPool pool;
    private String databaseUser;
    private String databasePass;
    private boolean databaseInitTables;
    private String databaseUrl;

    // Singleton
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Constructor privado
    private DatabaseManager() {
        loadProperties();

        connectionFactory = ConnectionFactories.get(databaseUrl);

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory).maxIdleTime(Duration.ofMillis(1000)).maxSize(20).build();
        pool = new ConnectionPool(configuration);

        if (databaseInitTables) {
            initTables();
        }
    }

    // Métodos que ejecuta los scripts de la base de datos
    public synchronized void initTables() {
        logger.debug("Borrando tablas si existe");
        excuteScript("remove.sql").block();
        logger.debug("Creando tablas");
        excuteScript("init.sql").block();
    }

    /**
     * Ejecuta un script
     *
     * @param script script a ejecutar
     * @return void
     */
    private Mono<Void> excuteScript(String script) {
        logger.debug("Ejecutando script: " + script);
        return Mono.usingWhen(connectionFactory.create(), connection -> {
            logger.debug("Creando conexión con la base de datos");
            String scriptContent = null;
            try {
                try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script)) {
                    if (inputStream == null) {
                        return Mono.error(new IOException("No se ha encontrado el fichero de scrript"));
                    } else {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                            scriptContent = reader.lines().collect(Collectors.joining("\n"));
                        }
                    }
                }
                logger.debug(scriptContent);
                Statement statement = connection.createStatement(scriptContent);
                return Mono.from(statement.execute());
            } catch (IOException e) {
                return Mono.error(e);
            }
        }, Connection::close).then();
    }

    // Métodos para cargar las propiedades de la base de datos
    private void loadProperties() {
        logger.debug("Cargando fichero de configuración de la base de datos");
        try {
            var pathFile = Paths.get("").toAbsolutePath().toString() + File.separator + "src" + File.separator  + File.separator + "main" + File.separator + "resources" + File.separator + "database.properties";
            var props = new Properties();
            props.load(new FileReader(pathFile));

            databaseUser = props.getProperty("database.user", "sa");
            databasePass = props.getProperty("database.password", "");
            databaseUrl = props.getProperty("database.url", "jdbc:h2:./funkos");
            databaseInitTables = Boolean.parseBoolean(props.getProperty("database.initTables", "false"));
            logger.debug("Configurado las properties correctamente");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Devuelve la conexión a la base de datos
     *
     * @return Connection de la base de datos
     */
    public ConnectionPool getConnectionPoll() {
        return this.pool;
    }
}
