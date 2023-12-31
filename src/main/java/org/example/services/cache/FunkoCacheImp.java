package org.example.services.cache;

import org.example.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunkoCacheImp implements FunkoCache {
    // Atributos
    private Logger logger = LoggerFactory.getLogger(FunkoCacheImp.class);
    private final int maxSize;
    private final Map<Long, Funko> cache;
    private final ScheduledExecutorService cleaner;

    // Constructor
    public FunkoCacheImp(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<Long, Funko>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Funko> eldest) {
                return size() > maxSize;
            }
        };
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Añade un funko a la cache
     *
     * @param key   clave
     * @param value valor
     * @return void
     */
    @Override
    public Mono<Void> put(Long key, Funko value) {
        logger.debug("Añadiendo funko a cache con id: " + key + " y valor: " + value);
        return Mono.fromRunnable(() -> cache.put(key, value));
    }

    /**
     * Obtiene un funko de la cache
     *
     * @param key clave
     * @return funko
     */
    @Override
    public Mono<Funko> get(Long key) {
        logger.debug("Obteniendo funko de cache con id: " + key);
        return Mono.justOrEmpty(cache.get(key));
    }

    /**
     * Elimina un funko de la cache
     *
     * @param key clave
     * @return void
     */
    @Override
    public Mono<Void> remove(Long key) {
        logger.debug("Eliminando funko de cache con id: " + key);
        return Mono.fromRunnable(() -> cache.remove(key));
    }

    // Elimina los funkos de la cache que lleven más de 90 segundos sin ser actualizados
    @Override
    public void clear() {
        cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdated_at().plusSeconds(90).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.debug("Autoeliminando por caducidad funko de cache con id: " + entry.getKey());
            }
            return shouldRemove;
        });
    }

    /**
     * Cierra la cache
     */
    @Override
    public void shutdown() {
        cleaner.shutdown();
    }
}
