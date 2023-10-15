package org.example.services.notificacion;

import org.example.model.Funko;
import org.example.model.Notificacion;
import reactor.core.publisher.Flux;

public interface FunkosNotificacion {
    // Atributos
    Flux<Notificacion<Funko>> getNotificacionFunko();
    void notify(Notificacion<Funko> notificacion);
}
