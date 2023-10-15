package org.example.services.funko;

import org.example.model.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FunkoService extends CrudFunkoService {
    // Metodos propios
    Flux<Funko> importarCSV() throws FileNotFoundException;

    Mono<Void> exportarJSON() throws IOException;

    Mono<Funko> findByUuid(UUID uuid);

    Mono<Funko> funkoMasCaro();

    Mono<Double> mediaFunkos();

    Mono<Map<String, List<Funko>>> funkoPorModelo();

    Mono<Map<String, Integer>> numeroFunkosPorModelo();

    Mono<List<Funko>> funkosLanzadosEn2023();

    Mono<Integer> numeroFunkosStitch();

    Mono<List<Funko>> funkosStitch();

}
