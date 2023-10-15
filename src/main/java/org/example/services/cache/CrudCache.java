package org.example.services.cache;

import reactor.core.publisher.Mono;

public interface CrudCache<K, V> {
    // Metodos CRUD
    Mono<Void> put(K key, V value);

    Mono<V> get(K key);

    Mono<Void> remove(K key);

    void clear();

    void shutdown();
}

