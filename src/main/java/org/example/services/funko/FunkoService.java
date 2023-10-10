package org.example.services.funko;

import org.example.model.Funko;
import reactor.core.publisher.Mono;

public interface FunkoService extends  CrudFunkoService{
    Mono<Funko> importarCSV();
    Mono<Void> exportarJSON();
}
